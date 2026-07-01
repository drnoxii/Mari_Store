// cargar productos index
function cargarProductos() {
    const contenedor = $('#lista-productos');

    if (contenedor.length === 0) {
        return;
    }

    fetch('/Mari_Store/AppController?action=listarProductos')
            .then(res => res.json())
            .then(productos => {
                contenedor.empty();

                productos.forEach(p => {
                    const idProducto = obtenerIdProducto(p);
                    const stockTotal = calcularStockTotal(p);
                    const precio = Number(p.precio) || 0;
                    const botonDeshabilitado = stockTotal <= 0 ? "disabled" : "";

                    const idCategoria =
                            p.categoria?.idCategoria ||
                            p.categoria?.idcategoria ||
                            p.idCategoria ||
                            p.idcategoria ||
                            p.id_categoria ||
                            "";

                    contenedor.append(`
                    <div class="col-12 col-sm-6 col-md-4 col-lg-3 producto" data-categoria="${idCategoria}">
                        <div class="card h-100 shadow-sm border-0">

                            <img src="${p.imagen}" 
                                 alt="${p.nombre}" 
                                 class="card-img-top p-2 img-product"/>

                            <div class="card-body d-flex flex-column">

                                <h6 class="card-title fw-bold">${p.nombre}</h6>

                                <p class="card-text text-muted small flex-grow-1">
                                    ${p.descripcion || ""}
                                </p>

                                <div class="mt-2">
                                    <div class="d-flex justify-content-between align-items-center">
                                        <span class="fs-5 fw-bold text-info">
                                            ${formatearPrecio(precio)}
                                        </span>

                                        <span class="badge ${claseStock(stockTotal)}">
                                            ${textoStock(stockTotal)}
                                        </span>
                                    </div>

                                    ${renderSelectVariantes(p, idProducto)}
                                </div>

                                <button onclick="agregarCarrito(${idProducto})"
                                        class="btn btn-info text-white w-100 mt-3"
                                        ${botonDeshabilitado}>
                                    <i class="bi bi-cart-plus me-2"></i>
                                    Agregar
                                </button>

                            </div>
                        </div>
                    </div>
                `);
                });

                const params = new URLSearchParams(window.location.search);
                const categoriaURL = params.get("categoria");

                if (categoriaURL) {
                    const tab = document.querySelector(`#tabs-categorias .nav-link[data-categoria="${categoriaURL}"]`);

                    if (tab) {
                        document.querySelectorAll("#tabs-categorias .nav-link").forEach(l => l.classList.remove("active"));
                        tab.classList.add("active");
                    }

                    filtrarProductosPorCategoria(categoriaURL);
                }
            })
            .catch(err => console.log("Error al cargar productos", err));
}


function inicializarFiltroCategorias() {
    const tabs = document.querySelectorAll("#tabs-categorias .nav-link");

    if (tabs.length === 0) {
        return;
    }

    tabs.forEach(link => {
        link.addEventListener("click", function (e) {
            e.preventDefault();

            tabs.forEach(l => l.classList.remove("active"));
            this.classList.add("active");

            const categoria = this.dataset.categoria;
            filtrarProductosPorCategoria(categoria);
        });
    });
}

function filtrarProductosPorCategoria(categoria) {
    document.querySelectorAll("#lista-productos .producto").forEach(producto => {
        const categoriaProducto = producto.dataset.categoria;

        if (categoria === "todos" || categoriaProducto === categoria) {
            producto.style.display = "block";
        } else {
            producto.style.display = "none";
        }
    });
}

// carrit

function agregarCarrito(idProducto) {
    const idDetalle = obtenerIdDetalleSeleccionado(idProducto);
    const selectVariante = document.getElementById(`variante-${idProducto}`);

    if (selectVariante && !idDetalle) {
        Swal.fire({
            icon: 'warning',
            title: 'Selecciona una variante',
            text: 'Debes elegir una talla/color antes de agregar al carrito.'
        });
        return;
    }

    let url = `AppController?action=addcarrito&id=${idProducto}`;

    if (idDetalle) {
        url += `&idDetalle=${idDetalle}`;
    }

    fetch(url, {method: 'POST'})
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    $('#cart-count').text(data.cartCount);

                    Swal.fire({
                        toast: true,
                        position: 'top-end',
                        icon: 'success',
                        title: 'Producto agregado',
                        showConfirmButton: false,
                        timer: 1500
                    });
                } else {
                    Swal.fire("Error", data.message || "No se pudo agregar el producto", "error");
                }
            })
            .catch(err => {
                console.error("Error al agregar carrito:", err);
                Swal.fire("Error", "Hubo un problema al agregar el producto", "error");
            });
}

function cargarCarrito() {
    const tabla = $('#tabla-carrito tbody');

    if (tabla.length === 0) {
        return;
    }

    fetch('AppController?action=listarCarrito')
            .then(res => res.json())
            .then(data => {
                tabla.empty();

                if (!data.items || data.items.length === 0) {
                    tabla.append(`
                        <tr>
                            <td colspan="6" class="text-center py-4">
                                El carrito está vacío
                            </td>
                        </tr>
                    `);

                    $('#resumen-total, #resumen-subtotal').text('S/ 0.00');
                    $('#cart-count').text('0');
                    return;
                }

                data.items.forEach((item, index) => {
                    const precio = Number(item.precioCompra) || 0;
                    const subTotal = Number(item.subTotal) || 0;
                    const talla = item.talla ? item.talla : "";
                    const color = item.color ? item.color : "";

                    let detalleVariante = "";

                    if (talla || color) {
                        detalleVariante = `
                            <small class="text-muted d-block">
                                ${talla} ${color ? " / " + color : ""}
                            </small>
                        `;
                    }

                    tabla.append(`
                        <tr>
                            <td>${index + 1}</td>

                            <td>
                                <span class="fw-bold">${item.nombre}</span>
                                ${detalleVariante}
                            </td>

                            <td>${formatearPrecio(precio)}</td>

                            <td>
                                <span class="badge bg-light text-dark border p-2">
                                    ${item.cantidad}
                                </span>
                            </td>

                            <td class="fw-bold">${formatearPrecio(subTotal)}</td>

                            <td>
                                <button class="btn btn-link text-danger p-0"
                                        onclick="eliminarItemCarrito(${item.idProducto || item.id_producto || 0}, ${item.idDetalle || item.id_detalle || 0})">
                                    <i class="bi bi-x-circle-fill"></i>
                                </button>
                            </td>
                        </tr>
                    `);
                });

                $('#resumen-total, #resumen-subtotal').text(formatearPrecio(data.total));
                $('#cart-count').text(data.items.length);
            })
            .catch(err => console.log("Error al cargar carrito", err));
}

function actualizarContadorCarrito() {
    fetch('AppController?action=listarCarrito')
            .then(res => res.json())
            .then(data => {
                $('#cart-count').text(data.items ? data.items.length : 0);
            })
            .catch(err => console.log("Error al actualizar contador", err));
}


function eliminarItemCarrito(idProducto, idDetalle) {
    let url = `AppController?action=delete&id=${idProducto}`;

    if (idDetalle) {
        url += `&idDetalle=${idDetalle}`;
    }

    fetch(url, {method: 'POST'})
            .then(res => res.json())
            .then(data => {
                if (data.success) {
                    cargarCarrito();
                    actualizarContadorCarrito();
                } else {
                    Swal.fire("Error", data.message || "No se pudo eliminar el producto", "error");
                }
            })
            .catch(err => {
                console.error("Error al eliminar item:", err);
                Swal.fire("Error", "No se pudo eliminar el producto del carrito", "error");
            });
}
// js de sesion
function verificarSesion() {
    const user = JSON.parse(sessionStorage.getItem("usuario"));

    if (user) {
        $('#btn-login-modal').addClass('d-none');
        $('#user-profile').removeClass('d-none');

        if (user.persona && user.persona.nombre) {
            $('#user-name').text(user.persona.nombre);
        } else {
            $('#user-name').text("Usuario");
        }

        if (user.rol === "ADMIN") {
            setTimeout(() => {
                const link = $('#link-admin');
                const sep = $('#separator-admin');

                if (link.length > 0) {
                    link.removeClass('d-none').attr('style', 'display: block !important');
                    sep.removeClass('d-none').attr('style', 'display: block !important');
                    console.log("✅ Menú de Admin mostrado con éxito");
                } else {
                    console.error("❌ Error: No se encontró el ID #link-admin en el DOM");
                }
            }, 300);
        }
    } else {
        $('#btn-login-modal').removeClass('d-none');
        $('#user-profile').addClass('d-none');
    }
}

function logout() {
    fetch('AuthController?action=Salir', {method: 'POST'})
            .then(() => {
                sessionStorage.clear();
                window.location.href = "index.html";
            })
            .catch(() => {
                sessionStorage.clear();
                window.location.href = "index.html";
            });
}
// login y registro
function inicializarEventosAuth() {
    $(document).off('submit', '#form-login');
    $(document).off('submit', '#form-register');

    $(document).on('submit', '#form-login', function (e) {
        e.preventDefault();

        const datos = $(this).serialize();

        fetch('/Mari_Store/AuthController?action=validar', {
            method: 'POST',
            body: new URLSearchParams(datos)
        })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        sessionStorage.setItem("usuario", JSON.stringify(data.userData));
                        location.reload();
                    } else {
                        Swal.fire("Error", data.message, "error");
                    }
                })
                .catch(err => {
                    console.error("Error login:", err);
                    Swal.fire("Error", "No se pudo iniciar sesión", "error");
                });
    });

    $(document).on('submit', '#form-register', function (e) {
        e.preventDefault();

        const datos = $(this).serialize();

        fetch('/Mari_Store/AuthController?action=register', {
            method: 'POST',
            body: new URLSearchParams(datos)
        })
                .then(res => res.json())
                .then(data => {
                    console.log("Respuesta registro:", data);

                    if (data.success) {
                        Swal.fire("¡Bienvenido!", data.message || "Registro exitoso", "success")
                                .then(() => {
                                    const modalRegister = bootstrap.Modal.getInstance(document.getElementById('modalRegister'));
                                    if (modalRegister) {
                                        modalRegister.hide();
                                    }

                                    const modalLogin = bootstrap.Modal.getOrCreateInstance(document.getElementById('modalLogin'));
                                    modalLogin.show();

                                    document.getElementById("form-register").reset();
                                });
                    } else {
                        Swal.fire("Error", data.message || "No se pudo registrar", "error");
                    }
                })
                .catch(err => {
                    console.error("Error registro:", err);
                    Swal.fire("Error", "No se pudo registrar el usuario", "error");
                });
    });
}

function ojitodepass() {
    const btn = document.getElementById("btnToggleLoginPassword");
    const input = document.getElementById("loginPassword");
    const icon = document.getElementById("iconLoginPassword");

    if (!btn || !input || !icon) {
        return;
    }

    btn.addEventListener("click", function () {
        if (input.type === "password") {
            input.type = "text";
            icon.classList.remove("bi-eye");
            icon.classList.add("bi-eye-slash");
        } else {
            input.type = "password";
            icon.classList.remove("bi-eye-slash");
            icon.classList.add("bi-eye");
        }
    });
}

// procesar compra
function procesarCompra() {
    const cantidadProductos = parseInt($('#cart-count').text()) || 0;

    if (cantidadProductos === 0) {
        Swal.fire({
            title: 'Carrito Vacío',
            text: "No puedes realizar una compra sin productos. ¡Ve a buscar algo que te guste!",
            icon: 'warning',
            confirmButtonColor: '#0dcaf0',
            confirmButtonText: 'Ir a la tienda'
        }).then(() => {
            window.location.href = "index.html";
        });

        return;
    }

    const user = JSON.parse(sessionStorage.getItem("usuario"));

    if (!user) {
        Swal.fire({
            title: 'Inicia Sesión',
            text: "Debes estar logueado para finalizar la compra",
            icon: 'info',
            showCancelButton: true,
            confirmButtonColor: '#0dcaf0',
            cancelButtonColor: '#6c757d',
            confirmButtonText: 'Ir al Login',
            cancelButtonText: 'Cancelar'
        }).then((result) => {
            if (result.isConfirmed) {
                $('#modalLogin').modal('show');
            }
        });

        return;
    }

    Swal.fire({
        title: '¿Confirmar Compra?',
        text: `Estás por comprar ${cantidadProductos} producto(s). ¿Deseas continuar?`,
        icon: 'question',
        showCancelButton: true,
        confirmButtonColor: '#0dcaf0',
        cancelButtonColor: '#6c757d',
        confirmButtonText: 'Sí, comprar ahora',
        cancelButtonText: 'Revisar más'
    }).then((result) => {
        if (result.isConfirmed) {
            Swal.fire({
                title: 'Procesando pedido...',
                didOpen: () => {
                    Swal.showLoading();
                },
                allowOutsideClick: false
            });

            fetch('AppController?action=GenerarCompra', {method: 'POST'})
                    .then(res => res.json())
                    .then(data => {
                        if (data.success) {
                            Swal.fire('¡Éxito!', data.message, 'success')
                                    .then(() => {
                                        $('#cart-count').text('0');
                                        window.location.href = "index.html";
                                    });
                        } else {
                            Swal.fire('Error', data.message, 'error');
                        }
                    })
                    .catch(err => {
                        console.error("Error compra:", err);
                        Swal.fire('Error', 'Hubo un problema en la conexión', 'error');
                    });
        }
    });
}
function finalizarCompra() {
    const metodoPago = document.getElementById("metodo_pago").value;
    const comprobante = document.getElementById("comprobante").value;

    if (!metodoPago) {
        Swal.fire("Aviso", "Seleccione un método de pago", "warning");
        return;
    }

    if (!comprobante.trim()) {
        Swal.fire("Aviso", "Ingrese el comprobante de pago", "warning");
        return;
    }

    const datos = new URLSearchParams();
    datos.append("metodo_pago", metodoPago);
    datos.append("comprobante", comprobante);

    fetch("/Mari_Store/AppController?action=generarcompra", {
        method: "POST",
        body: datos
    })
            .then(res => res.json())
            .then(data => {
                console.log("Respuesta compra:", data);

                if (data.success) {
                    Swal.fire("Compra registrada", data.message, "success")
                            .then(() => {
                                window.location.href = "mis-compras.html";
                            });
                } else {
                    Swal.fire("Error", data.message, "error");
                }
            })
            .catch(err => {
                console.error("Error al finalizar compra:", err);
                Swal.fire("Error", "No se pudo finalizar la compra", "error");
            });
}

// admin productos

function cargarTablaAdmin() {
    const tablaElemento = $('#tabla-productos');

    if (tablaElemento.length === 0) {
        return;
    }

    if (!$.fn.DataTable) {
        console.error("DataTable no está cargado");
        return;
    }

    if ($.fn.DataTable.isDataTable('#tabla-productos')) {
        tablaElemento.DataTable().destroy();
    }

    tablaElemento.DataTable({
        "ajax": {
            "url": "ProductoController?action=listar",
            "dataSrc": ""
        },
        "columns": [
            {
                "data": null,
                "render": function (data, type, row, meta) {
                    return meta.row + 1;
                }
            },
            {
                "data": "imagen",
                "render": function (data) {
                    return `<img src="${data}" width="50" class="img-thumbnail shadow-sm">`;
                }
            },
            {
                "data": "nombre"
            },
            {
                "data": "precio",
                "render": function (data) {
                    return `<b>${formatearPrecio(data)}</b>`;
                }
            },
            {
                "data": null,
                "render": function (data) {
                    const stockTotal = calcularStockTotal(data);
                    const variantes = obtenerVariantes(data);

                    if (variantes.length === 0) {
                        return `
                            <span class="badge ${claseStock(stockTotal)}">
                                ${textoStock(stockTotal)}
                            </span>
                        `;
                    }

                    const detalleVariantes = variantes.map(v => {
                        const talla = v.talla || "Sin talla";
                        const color = v.color || "Sin color";
                        const stock = Number(v.stock) || 0;

                        return `
                            <div class="small mb-1">
                                <span class="badge ${claseStock(stock)}">
                                    ${talla} / ${color}: ${stock}
                                </span>
                            </div>
                        `;
                    }).join("");

                    return `
                        <div>
                            <span class="badge bg-primary">
                                Total: ${stockTotal}
                            </span>

                            <div class="mt-1">
                                ${detalleVariantes}
                            </div>
                        </div>
                    `;
                }
            },
            {
                "data": null,
                "render": function (data) {
                    const idProducto = obtenerIdProducto(data);

                    return `
                        <div class="btn-group">
                            <button class="btn btn-warning btn-sm" onclick="abrirModalEditar(${idProducto})">

                            <button class="btn btn-danger btn-sm" onclick="eliminarProducto(${idProducto})">
                                <i class="bi bi-trash"></i>
                            </button>
                        </div>
                    `;
                }
            }
        ],
        "language": {
            "lengthMenu": "Mostrar _MENU_ registros",
            "zeroRecords": "No se encontraron resultados",
            "info": "Mostrando registros del _START_ al _END_ de un total de _TOTAL_ registros",
            "infoEmpty": "Mostrando registros del 0 al 0 de un total de 0 registros",
            "infoFiltered": "(filtrado de un total de _MAX_ registros)",
            "sSearch": "Buscar:",
            "oPaginate": {
                "sFirst": "Primero",
                "sLast": "Último",
                "sNext": "Siguiente",
                "sPrevious": "Anterior"
            },
            "sProcessing": "Procesando..."
        }
    });
}


function cargarProductosAdmin() {
    const tbody = $('#tabla-productos tbody');

    if (tbody.length === 0) {
        return;
    }

    fetch('/Mari_Store/ProductoController?action=listar')
            .then(res => res.json())
            .then(productos => {
                tbody.empty();

                if (!productos || productos.length === 0) {
                    tbody.append(`
                    <tr>
                        <td colspan="6" class="text-center text-muted">
                            No hay productos registrados
                        </td>
                    </tr>
                `);
                    return;
                }

                productos.forEach((p, index) => {
                    const idProducto = obtenerIdProducto(p);
                    const stockTotal = calcularStockTotal(p);
                    const precio = Number(p.precio) || 0;

                    let variantesHtml = "";

                    if (p.variantes && p.variantes.length > 0) {
                        p.variantes.forEach(v => {
                            variantesHtml += `
                            <span class="badge bg-light text-dark border me-1 mb-1">
                                ${v.talla} / ${v.color}: ${v.stock}
                            </span>
                        `;
                        });
                    } else {
                        variantesHtml = `<span class="text-muted small">Sin variantes</span>`;
                    }

                    tbody.append(`
                    <tr>
                        <td>${index + 1}</td>

                        <td>
                            <img src="${p.imagen}" 
                                 width="60" 
                                 height="60" 
                                 style="object-fit: cover; border-radius: 8px;">
                        </td>

                        <td>
                            <strong>${p.nombre}</strong>
                            <br>
                            <small class="text-muted">${p.descripcion || ""}</small>
                            <div class="mt-2">
                                ${variantesHtml}
                            </div>
                        </td>

                        <td>${formatearPrecio(precio)}</td>

                        <td>
                            <span class="badge ${claseStock(stockTotal)}">
                                ${textoStock(stockTotal)}
                            </span>
                        </td>

                        <td>
                            <button class="btn btn-warning btn-sm me-1"
                                    onclick="abrirModalEditar(${idProducto})">
                                <i class="bi bi-pencil-square"></i>
                            </button>

                            <button class="btn btn-danger btn-sm"
                                    onclick="eliminarProducto(${idProducto})">
                                <i class="bi bi-trash"></i>
                            </button>
                        </td>
                    </tr>
                `);
                });
            })
            .catch(err => {
                console.log("Error al cargar productos admin", err);
            });
}

// modal producto

function abrirModalNuevo() {
    document.getElementById("tituloModal").textContent = "Nuevo Producto";
    document.getElementById("action").value = "guardar";
    document.getElementById("id_producto").value = "";
    document.getElementById("imagenActual").value = "";

    document.getElementById("form-producto").reset();

    limpiarVariantes();
    agregarVariante();

    const modal = new bootstrap.Modal(document.getElementById("modalProducto"));
    modal.show();
}
const formProducto = document.getElementById("form-producto");

if (formProducto) {
    formProducto.addEventListener("submit", function (e) {
        e.preventDefault();

        const action = document.getElementById("action").value;
        const formData = new FormData(formProducto);

        let url = "/Mari_Store/ProductoController?action=" + action;

        fetch(url, {
            method: "POST",
            body: formData
        })
                .then(response => response.json())
                .then(data => {
                    console.log("Respuesta producto:", data);

                    if (data === true || data.success === true) {
                        Swal.fire({
                            icon: "success",
                            title: action === "guardar" ? "Producto registrado" : "Producto actualizado",
                            text: action === "guardar"
                                    ? "El producto se guardó correctamente."
                                    : "El producto se actualizó correctamente.",
                            timer: 1500,
                            showConfirmButton: false
                        });

                        const modalProducto = bootstrap.Modal.getInstance(
                                document.getElementById("modalProducto")
                                );

                        if (modalProducto) {
                            modalProducto.hide();
                        }

                        formProducto.reset();

                        if (typeof limpiarVariantes === "function") {
                            limpiarVariantes();
                        }

                        if ($.fn.DataTable && $.fn.DataTable.isDataTable("#tabla-productos")) {
                            $("#tabla-productos").DataTable().ajax.reload(null, false);
                        } else {
                            cargarProductosAdmin();
                        }

                    } else {
                        Swal.fire(
                                "Error",
                                data.message || "No se pudo guardar el producto",
                                "error"
                                );
                    }
                })
                .catch(error => {
                    console.error("Error al guardar producto:", error);
                    Swal.fire("Error", "No se pudo conectar con el controlador", "error");
                });
    });
}

function llenarVariantesProducto(producto) {
    limpiarVariantes();

    if (producto.variantes && producto.variantes.length > 0) {
        producto.variantes.forEach(v => {
            agregarVariante(v.talla, v.color, v.stock);
        });
    } else {
        agregarVariante();
    }
}

// editar producto
function editarProducto(id) {
    fetch(`ProductoController?action=buscar&id=${id}`)
            .then(res => res.json())
            .then(p => {
                const stockTotal = calcularStockTotal(p);

                $('#id_producto').val(obtenerIdProducto(p));
                $('#nombre').val(p.nombre);
                $('#descripcion').val(p.descripcion);
                $('#precio').val(p.precio);
                $('#stock').val(stockTotal);
                $('#action').val('editar');
                $('#tituloModal').text('Editar Producto');
                $('#modalProducto').modal('show');
            })
            .catch(err => {
                console.error("Error al buscar producto:", err);
                Swal.fire("Error", "No se pudo obtener los datos del producto", "error");
            });
}

function abrirModalEditar(idProducto) {
    fetch("/Mari_Store/ProductoController?action=buscar&id=" + idProducto)
            .then(response => response.json())
            .then(producto => {
                document.getElementById("tituloModal").textContent = "Editar Producto";
                document.getElementById("action").value = "editar";

                document.getElementById("id_producto").value = producto.idProducto;
                document.getElementById("nombre").value = producto.nombre;
                document.getElementById("descripcion").value = producto.descripcion;
                document.getElementById("precio").value = producto.precio;
                document.getElementById("imagenActual").value = producto.imagen;

                if (producto.categoria) {
                    document.getElementById("idCategoria").value = producto.categoria.idCategoria;
                }

                llenarVariantesProducto(producto);

                const modal = new bootstrap.Modal(document.getElementById("modalProducto"));
                modal.show();
            })
            .catch(error => {
                console.error("Error al buscar producto:", error);
                alert("No se pudo cargar el producto");
            });
}

// eliminar producto


function eliminarProducto(id) {
    Swal.fire({
        title: "¿Estás seguro?",
        text: "Si el producto ya tiene ventas registradas, no se podrá eliminar.",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Sí, eliminar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`/Mari_Store/ProductoController?action=eliminar&id=${id}`, {
                method: "POST"
            })
                    .then(res => res.json())
                    .then(res => {
                        console.log("Respuesta eliminar:", res);

                        if (res === true || res.success === true) {
                            Swal.fire("Eliminado", "El producto ha sido borrado.", "success");

                            if ($.fn.DataTable && $.fn.DataTable.isDataTable("#tabla-productos")) {
                                $("#tabla-productos").DataTable().ajax.reload(null, false);
                            } else {
                                cargarProductosAdmin();
                            }

                        } else {
                            Swal.fire(
                                    "No se puede eliminar",
                                    res.message || "Este producto probablemente ya tiene ventas registradas.",
                                    "warning"
                                    );
                        }
                    })
                    .catch(err => {
                        console.error("Error eliminar producto:", err);
                        Swal.fire("Error", "No se pudo eliminar el producto.", "error");
                    });
        }
    });
}

function abrirModalPago() {
    // 1. Validar si hay usuario logueado
    fetch("/Mari_Store/AppController?action=perfilUsuario")
            .then(response => response.json())
            .then(dataUsuario => {

                if (!dataUsuario.success) {
                    Swal.fire({
                        title: "Inicia sesión",
                        text: "Debes iniciar sesión para finalizar la compra",
                        icon: "warning",
                        confirmButtonText: "Iniciar sesión"
                    }).then(() => {
                        const modalLogin = bootstrap.Modal.getOrCreateInstance(
                                document.getElementById("modalLogin")
                                );
                        modalLogin.show();
                    });

                    return;
                }

                // 2. Validar si el carrito tiene productos
                fetch("/Mari_Store/AppController?action=listarCarrito")
                        .then(response => response.json())
                        .then(dataCarrito => {

                            if (!dataCarrito.items || dataCarrito.items.length === 0) {
                                Swal.fire({
                                    title: "Carrito vacío",
                                    text: "Agrega productos antes de finalizar la compra",
                                    icon: "warning",
                                    confirmButtonText: "Ver productos"
                                }).then(() => {
                                    window.location.href = "productos.html";
                                });

                                return;
                            }

                            // 3. Limpiar modal antes de abrir
                            document.getElementById("form-pago").reset();
                            document.getElementById("contenedor-qr").classList.add("d-none");
                            document.getElementById("imagen-qr").src = "";
                            document.getElementById("texto-metodo").textContent = "";

                            // 4. Recién abre modal de pago
                            const modalPago = bootstrap.Modal.getOrCreateInstance(
                                    document.getElementById("modalPago")
                                    );
                            modalPago.show();
                        })
                        .catch(error => {
                            console.error("Error al validar carrito:", error);
                            Swal.fire("Error", "No se pudo validar el carrito", "error");
                        });

            })
            .catch(error => {
                console.error("Error al validar usuario:", error);
                Swal.fire("Error", "No se pudo validar la sesión", "error");
            });
}

const selectMetodoPago = document.getElementById("metodo_pago");

if (selectMetodoPago) {
    selectMetodoPago.addEventListener("change", function () {
        const metodo = this.value;
        const contenedorQR = document.getElementById("contenedor-qr");
        const imagenQR = document.getElementById("imagen-qr");
        const textoMetodo = document.getElementById("texto-metodo");

        if (!contenedorQR || !imagenQR || !textoMetodo) {
            return;
        }

        if (metodo === "YAPE") {
            imagenQR.src = "/Mari_Store/assets/img/pagos/yape-qr.png";
            textoMetodo.textContent = "Paga con Yape y sube la captura de la operación.";
            contenedorQR.classList.remove("d-none");

        } else if (metodo === "PLIN") {
            imagenQR.src = "/Mari_Store/assets/img/pagos/plin-qr.png";
            textoMetodo.textContent = "Paga con Plin y sube la captura de la operación.";
            contenedorQR.classList.remove("d-none");

        } else {
            imagenQR.src = "";
            textoMetodo.textContent = "";
            contenedorQR.classList.add("d-none");
        }
    });
}

const formPago = document.getElementById("form-pago");

if (formPago) {
    formPago.addEventListener("submit", function (e) {
        e.preventDefault();

        const metodoPago = document.getElementById("metodo_pago").value;
        const comprobante = document.getElementById("comprobante").files[0];

        if (!metodoPago) {
            Swal.fire("Aviso", "Seleccione un método de pago", "warning");
            return;
        }

        if (!comprobante) {
            Swal.fire("Aviso", "Debe subir la captura del comprobante", "warning");
            return;
        }

        const formData = new FormData();
        formData.append("metodo_pago", metodoPago);
        formData.append("comprobante", comprobante);

        fetch("/Mari_Store/AppController?action=generarcompra", {
            method: "POST",
            body: formData
        })
                .then(res => res.json())
                .then(data => {
                    console.log("Respuesta compra:", data);

                    if (data.success) {
                        Swal.fire({
                            title: "Pedido registrado",
                            text: "Tu pedido está pendiente de aprobación. Puedes verlo en Mis Compras.",
                            icon: "success"
                        }).then(() => {
                            window.location.href = "mis_compras.html";
                        });
                    } else {
                        Swal.fire("Error", data.message || "No se pudo finalizar la compra", "error");
                    }
                })
                .catch(error => {
                    console.error("Error al finalizar compra:", error);
                    Swal.fire("Error", "No se pudo finalizar la compra", "error");
                });
    });
}


function obtenerVariantes(p) {
    return p.variantes || p.detalles || p.detalleProductos || [];
}

function obtenerIdProducto(p) {
    return p.id_producto || p.idProducto || p.id || 0;
}

function obtenerIdDetalle(v) {
    return v.idDetalle || v.id_detalle || v.id || 0;
}

function calcularStockTotal(p) {
    const variantes = obtenerVariantes(p);

    if (variantes.length > 0) {
        return variantes.reduce((total, v) => total + (Number(v.stock) || 0), 0);
    }

    return Number(p.stock) || 0;
}

function claseStock(stock) {
    if (stock > 10) {
        return "bg-light text-success border border-success";
    }

    if (stock > 0) {
        return "bg-light text-warning border border-warning";
    }

    return "bg-light text-danger border border-danger";
}

function textoStock(stock) {
    if (stock > 10) {
        return `Stock: ${stock}`;
    }

    if (stock > 0) {
        return `Últimos ${stock}`;
    }

    return "Sin stock";
}

function formatearPrecio(precio) {
    return `S/ ${Number(precio || 0).toFixed(2)}`;
}

function renderSelectVariantes(p, idProducto) {
    const variantes = obtenerVariantes(p);

    if (variantes.length === 0) {
        return "";
    }

    const primeraDisponible = variantes.findIndex(v => Number(v.stock) > 0);

    const opciones = variantes.map((v, index) => {
        const idDetalle = obtenerIdDetalle(v);
        const talla = v.talla || "Sin talla";
        const color = v.color || "Sin color";
        const stock = Number(v.stock) || 0;

        const disabled = stock <= 0 ? "disabled" : "";
        const selected = index === primeraDisponible ? "selected" : "";

        return `
            <option value="${idDetalle}" data-stock="${stock}" ${disabled} ${selected}>
                ${talla} / ${color} - Stock: ${stock}
            </option>
        `;
    }).join("");

    const stockInicial = primeraDisponible >= 0
            ? Number(variantes[primeraDisponible].stock) || 0
            : 0;

    return `
        <div class="mt-2">
            <select class="form-select form-select-sm"
                    id="variante-${idProducto}"
                    onchange="actualizarStockSeleccionado(${idProducto})">
                ${opciones}
            </select>

            <small id="stock-variante-${idProducto}" class="text-muted d-block mt-1">
                Stock de variante: ${stockInicial}
            </small>
        </div>
    `;
}

function actualizarStockSeleccionado(idProducto) {
    const select = document.getElementById(`variante-${idProducto}`);
    const texto = document.getElementById(`stock-variante-${idProducto}`);

    if (!select || !texto) {
        return;
    }

    const stock = select.options[select.selectedIndex].dataset.stock || 0;
    texto.textContent = `Stock de variante: ${stock}`;
}

function obtenerIdDetalleSeleccionado(idProducto) {
    const select = document.getElementById(`variante-${idProducto}`);

    if (!select) {
        return "";
    }

    return select.value;
}


