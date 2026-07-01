
let comprasUsuario = [];

console.log("Script de mis compras cargado");
cargarMisCompras();

function cargarMisCompras() {
    console.log("Ejecutando cargarMisCompras...");

    fetch("/Mari_Store/AppController?action=misCompras")
        .then(response => response.text())
        .then(texto => {
            console.log("Respuesta cruda misCompras:", texto);

            const data = JSON.parse(texto);
            console.log("Mis compras:", data);

            const tbody = document.getElementById("tbody-compras");
            tbody.innerHTML = "";

            if (!data.success) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-danger">
                            ${data.message || "No se pudieron cargar tus compras"}
                        </td>
                    </tr>
                `;
                return;
            }

            if (!data.compras || data.compras.length === 0) {
                tbody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-muted">
                            No tienes compras registradas
                        </td>
                    </tr>
                `;
                return;
            }

            comprasUsuario = data.compras;

            comprasUsuario.forEach((pedido, index) => {
                tbody.innerHTML += `
                    <tr>
                        <td>${pedido.idPedido}</td>
                        <td>${pedido.fechaPago || pedido.fecha || "Sin fecha"}</td>
                        <td>S/ ${Number(pedido.total || 0).toFixed(2)}</td>

                        <td>
                            <span class="badge bg-info text-dark">
                                ${pedido.metodoPago || "Sin pago"}
                            </span>
                        </td>

                        <td>${badgeEstado(pedido.estado)}</td>

                        <td>
                            ${
                                pedido.comprobante
                                ? `
                                    <button type="button"
                                            class="btn btn-outline-primary btn-sm"
                                            onclick="verComprobante('${normalizarRutaComprobante(pedido.comprobante)}')">
                                        <i class="bi bi-image me-1"></i> Ver
                                    </button>
                                  `
                                : `<span class="text-muted">Sin comprobante</span>`
                            }
                        </td>

                        <td>
                            <button type="button" class="btn btn-info btn-sm text-white"
                                    onclick="verDetalleCompra(${index})">
                                <i class="bi bi-eye me-1"></i> Ver detalle
                            </button>
                        </td>
                    </tr>
                `;
            });
        })
        .catch(error => {
            console.error("Error al cargar compras:", error);

            document.getElementById("tbody-compras").innerHTML = `
                <tr>
                    <td colspan="7" class="text-center text-danger">
                        Error al cargar tus compras
                    </td>
                </tr>
            `;
        });
}

function badgeEstado(estado) {
    if (!estado) {
        return `<span class="badge bg-secondary">SIN ESTADO</span>`;
    }

    const estadoUpper = estado.toUpperCase();

    switch (estadoUpper) {
        case "PENDIENTE":
            return `<span class="badge bg-warning text-dark">PENDIENTE DE APROBACIÓN</span>`;

        case "PROCESADO":
            return `<span class="badge bg-primary">PROCESADO</span>`;

        case "ENVIADO":
            return `<span class="badge bg-info text-dark">ENVIADO</span>`;

        case "ENTREGADO":
            return `<span class="badge bg-success">ENTREGADO</span>`;

        case "CANCELADO":
            return `<span class="badge bg-danger">CANCELADO</span>`;

        default:
            return `<span class="badge bg-secondary">${estadoUpper}</span>`;
    }
}

function verDetalleCompra(index) {
    const pedido = comprasUsuario[index];
    const detalle = pedido.detalle || [];

    const contenedorDetalle = document.getElementById("detalle-compra");
    const tbodyDetalle = document.getElementById("tbody-detalle-compra");

    tbodyDetalle.innerHTML = "";

    if (detalle.length === 0) {
        tbodyDetalle.innerHTML = `
            <tr>
                <td colspan="6" class="text-center text-muted">
                    Este pedido no tiene detalle disponible
                </td>
            </tr>
        `;
    } else {
        detalle.forEach(item => {
            tbodyDetalle.innerHTML += `
                <tr>
                    <td>${item.nombreProducto || item.nombre || ""}</td>
                    <td>${item.talla || ""}</td>
                    <td>${item.color || ""}</td>
                    <td>${item.cantidad}</td>
                    <td>S/ ${Number(item.precioUnitario || item.precioCompra || 0).toFixed(2)}</td>
                    <td>S/ ${Number(item.subtotal || item.subTotal || 0).toFixed(2)}</td>
                </tr>
            `;
        });
    }

    contenedorDetalle.classList.remove("d-none");
    contenedorDetalle.scrollIntoView({ behavior: "smooth" });
}

function normalizarRutaComprobante(ruta) {
    if (!ruta || ruta.trim() === "") {
        return "";
    }

    if (ruta.startsWith("/Mari_Store/")) {
        return ruta;
    }

    if (ruta.startsWith("assets/")) {
        return "/Mari_Store/" + ruta;
    }

    return "/Mari_Store/assets/img/comprobantes/" + ruta;
}

function verComprobante(rutaImagen) {
    if (!rutaImagen || rutaImagen.trim() === "") {
        Swal.fire("Aviso", "Este pedido no tiene comprobante", "warning");
        return;
    }

    document.getElementById("imagen-comprobante").src = rutaImagen;

    const modal = bootstrap.Modal.getOrCreateInstance(
        document.getElementById("modalComprobante")
    );

    modal.show();
}

const modalComprobante = document.getElementById("modalComprobante");

if (modalComprobante) {
    modalComprobante.addEventListener("hidden.bs.modal", function () {
        document.getElementById("imagen-comprobante").src = "";
    });
}



// cargar listado de cliente


document.addEventListener("DOMContentLoaded", function () {
    cargarPerfilUsuario();
});

function cargarPerfilUsuario() {
    fetch("/Mari_Store/AppController?action=perfilUsuario")
        .then(response => response.json())
        .then(data => {
            console.log("Perfil usuario:", data);

            if (!data.success) {
                Swal.fire("Aviso", data.message || "Debe iniciar sesión", "warning")
                    .then(() => {
                        window.location.href = "index.html";
                    });
                return;
            }

            const usuario = data.usuario;

            document.getElementById("perfil-nombre").textContent = usuario.nombre || "";
            document.getElementById("perfil-apellidos").textContent = usuario.apellidos || "";
            document.getElementById("perfil-dni").textContent = usuario.dni || "";
            document.getElementById("perfil-telefono").textContent = usuario.telefono || "";
            document.getElementById("perfil-fecha").textContent = usuario.fechaNacimiento || "No registrado";
            document.getElementById("perfil-email").textContent = usuario.email || "";
            document.getElementById("perfil-rol").textContent = usuario.rol || "";

            document.getElementById("perfil-loading").style.display = "none";
            document.getElementById("perfil-contenido").style.display = "block";
        })
        .catch(error => {
            console.error("Error al cargar perfil:", error);

            document.getElementById("perfil-loading").style.display = "none";
            document.getElementById("perfil-error").classList.remove("d-none");
        });
}
 
