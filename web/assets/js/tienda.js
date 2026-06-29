/* 
 * tienda.js
 * Lógica de productos, carrito, sesión, login, registro y admin.
 * Adaptado para manejar stock por variantes.
 */

/* ========================= */
/* HELPERS PARA PRODUCTOS */
/* ========================= */

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

/* ========================= */
/* PRODUCTOS INDEX */
/* ========================= */

function cargarProductos() {
    const contenedor = $('#lista-productos');

    if (contenedor.length === 0) {
        return;
    }

    fetch('AppController?action=listarProductos')
            .then(res => res.json())
            .then(productos => {
                contenedor.empty();

                productos.forEach(p => {
                    const idProducto = obtenerIdProducto(p);
                    const stockTotal = calcularStockTotal(p);
                    const precio = Number(p.precio) || 0;
                    const botonDeshabilitado = stockTotal <= 0 ? "disabled" : "";

                    contenedor.append(`
                        <div class="col-12 col-sm-6 col-md-4 col-lg-3">
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
            })
            .catch(err => console.log("Error al cargar productos", err));
}

/* ========================= */
/* CARRITO */
/* ========================= */

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

/*
 * Ajusta el action si tu AppController usa otro nombre.
 * Por ejemplo: QuitarCarrito, EliminarCarrito, DeleteCarrito, etc.
 */
function eliminarItemCarrito(idProducto, idDetalle) {
    let url = `AppController?action=EliminarCarrito&id=${idProducto}`;

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

/* ========================= */
/* SESIÓN */
/* ========================= */

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

/* ========================= */
/* LOGIN Y REGISTRO */
/* ========================= */

function inicializarEventosAuth() {
    $(document).off('submit', '#form-login');
    $(document).off('submit', '#form-register');

    $(document).on('submit', '#form-login', function (e) {
        e.preventDefault();

        const datos = $(this).serialize();

        fetch('AuthController?action=validar', {
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

        fetch('AuthController?action=register', {
            method: 'POST',
            body: new URLSearchParams(datos)
        })
                .then(res => res.json())
                .then(data => {
                    if (data.success) {
                        Swal.fire("¡Bienvenido!", "Registro exitoso", "success")
                                .then(() => {
                                    $('#modalRegister').modal('hide');
                                    $('#modalLogin').modal('show');
                                });
                    } else {
                        Swal.fire("Error", data.message, "error");
                    }
                })
                .catch(err => {
                    console.error("Error registro:", err);
                    Swal.fire("Error", "No se pudo registrar el usuario", "error");
                });
    });
}

/* ========================= */
/* PROCESAR COMPRA */
/* ========================= */

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

/* ========================= */
/* ADMIN PRODUCTOS */
/* ========================= */

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
                            <button class="btn btn-warning btn-sm" onclick="editarProducto(${idProducto})">
                                <i class="bi bi-pencil-square"></i>
                            </button>

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

/* ========================= */
/* MODAL PRODUCTO */
/* ========================= */

function abrirModalNuevo() {
    $('#form-producto')[0].reset();
    $('#action').val('guardar');
    $('#tituloModal').text('Nuevo Producto');
    $('#modalProducto').modal('show');
}

$(document).off('submit', '#form-producto');

$(document).on('submit', '#form-producto', function (e) {
    e.preventDefault();

    const formData = new FormData(this);

    fetch('ProductoController', {
        method: 'POST',
        body: formData
    })
            .then(res => res.json())
            .then(res => {
                if (res) {
                    Swal.fire("¡Éxito!", "Producto guardado correctamente", "success");
                    $('#modalProducto').modal('hide');
                    cargarTablaAdmin();
                } else {
                    Swal.fire("Error", "No se pudo guardar el producto", "error");
                }
            })
            .catch(err => {
                console.error("Error guardar producto:", err);
                Swal.fire("Error", "No se pudo guardar el producto", "error");
            });
});

/* ========================= */
/* EDITAR PRODUCTO */
/* ========================= */

function editarProducto(id) {
    fetch(`ProductoController?action=buscar&id=${id}`)
            .then(res => res.json())
            .then(p => {
                const stockTotal = calcularStockTotal(p);

                $('#id_producto').val(obtenerIdProducto(p));
                $('#nombre').val(p.nombre);
                $('#descripcion').val(p.descripcion);
                $('#precio').val(p.precio);

                /*
                 * Si tu formulario todavía tiene un input #stock simple,
                 * aquí se muestra el stock total calculado de todas las variantes.
                 */
                $('#stock').val(stockTotal);

                /*
                 * Si luego agregas campos para editar variantes en el modal,
                 * aquí se puede llenar dinámicamente con p.variantes.
                 */

                $('#action').val('editar');
                $('#tituloModal').text('Editar Producto');
                $('#modalProducto').modal('show');
            })
            .catch(err => {
                console.error("Error al buscar producto:", err);
                Swal.fire("Error", "No se pudo obtener los datos del producto", "error");
            });
}

/* ========================= */
/* ELIMINAR PRODUCTO */
/* ========================= */

function eliminarProducto(id) {
    Swal.fire({
        title: '¿Estás seguro?',
        text: "¡No podrás revertir esto!",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#3085d6',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then((result) => {
        if (result.isConfirmed) {
            fetch(`ProductoController?action=eliminar&id=${id}`, {method: 'POST'})
                    .then(res => res.json())
                    .then(res => {
                        if (res) {
                            Swal.fire('Eliminado', 'El producto ha sido borrado.', 'success');
                            cargarTablaAdmin();
                        } else {
                            Swal.fire('Error', 'No se pudo eliminar el producto.', 'error');
                        }
                    })
                    .catch(err => {
                        console.error("Error eliminar producto:", err);
                        Swal.fire('Error', 'No se pudo eliminar el producto.', 'error');
                    });
        }
    });
}