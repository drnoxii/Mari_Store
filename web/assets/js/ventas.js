
document.addEventListener("DOMContentLoaded", function () {
    cargarPagosPendientes();
});

function cargarPagosPendientes() {
    fetch("/Mari_Store/PagoController?action=listarPagosPendientes")
            .then(response => response.json())
            .then(data => {
                const tbody = document.getElementById("tbody-pagos-pendientes");
                tbody.innerHTML = "";
                if (!data.success || !data.data || data.data.length === 0) {
                    tbody.innerHTML = `
                        <tr>
                            <td colspan="7" class="text-center text-muted">
                                No hay pagos pendientes
                            </td>
                        </tr>
                    `;
                    return;
                }

                data.data.forEach(pago => {
                    tbody.innerHTML += `
            <tr>
                <td>${pago.idPedido}</td>
                <td>${pago.idPago}</td>
                <td>${pago.metodoPago}</td>
                <td>S/ ${Number(pago.totalPedido).toFixed(2)}</td>
                <td>S/ ${Number(pago.monto).toFixed(2)}</td>

                <td>
                    <button type="button"
                            class="btn btn-outline-primary btn-sm"
                            onclick="verComprobante('${normalizarRutaComprobante(pago.comprobante)}')">
                        <i class="bi bi-image me-1"></i> Ver comprobante
                    </button>
                </td>

                <td>
                    <button type="button" class="btn btn-success btn-sm"
                            onclick="aprobarPago(${pago.idPedido})">
                        <i class="bi bi-check-circle me-1"></i> Aprobar
                    </button>
                </td>
            </tr>
        `;
                });
            })
            .catch(error => {
                console.error("Error al listar pagos pendientes:", error);
                const tbody = document.getElementById("tbody-pagos-pendientes");
                tbody.innerHTML = `
                    <tr>
                        <td colspan="7" class="text-center text-danger">
                            Error al cargar pagos pendientes
                        </td>
                    </tr>
                `;
            });
}
function normalizarRutaComprobante(ruta) {
    if (!ruta || ruta.trim() === "") {
        return "";
    }

    /*
     Si ya viene con /Mari_Store, la dejamos igual.
     Ejemplo: /Mari_Store/assets/img/comprobantes/foto.jpg
     */
    if (ruta.startsWith("/Mari_Store/")) {
        return ruta;
    }

    /*
     Si viene como assets/img/comprobantes/foto.jpg,
     le agregamos el contexto del proyecto.
     */
    if (ruta.startsWith("assets/")) {
        return "/Mari_Store/" + ruta;
    }

    /*
     Si solo viene el nombre del archivo, asumimos que está en comprobantes.
     */
    return "/Mari_Store/assets/img/comprobantes/" + ruta;
}

function verComprobante(rutaImagen) {
    if (!rutaImagen || rutaImagen.trim() === "") {
        Swal.fire("Aviso", "Este pago no tiene comprobante registrado", "warning");
        return;
    }

    const imagen = document.getElementById("imagen-comprobante");
    imagen.src = rutaImagen;
    const modal = bootstrap.Modal.getOrCreateInstance(
            document.getElementById("modalComprobante")
            );
    modal.show();
}

function aprobarPago(idPedido) {
    Swal.fire({
        title: "¿Aprobar pago?",
        text: "Al aprobar este pago, el pedido pasará a estado procesado.",
        icon: "question",
        showCancelButton: true,
        confirmButtonColor: "#198754",
        cancelButtonColor: "#6c757d",
        confirmButtonText: "Sí, aprobar",
        cancelButtonText: "Cancelar"
    }).then((result) => {
        if (result.isConfirmed) {

            Swal.fire({
                title: "Procesando...",
                text: "Aprobando el pago, espera un momento.",
                allowOutsideClick: false,
                didOpen: () => {
                    Swal.showLoading();
                }
            });
            fetch("/Mari_Store/PagoController?action=aprobarPago&idPedido=" + idPedido, {
                method: "POST"
            })
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            Swal.fire({
                                title: "Pago aprobado",
                                text: data.message || "El pedido fue aprobado correctamente.",
                                icon: "success",
                                confirmButtonColor: "#198754"
                            }).then(() => {
                                cargarPagosPendientes();
                            });
                        } else {
                            Swal.fire({
                                title: "No se pudo aprobar",
                                text: data.message || "Ocurrió un problema al aprobar el pago.",
                                icon: "error",
                                confirmButtonColor: "#dc3545"
                            });
                        }
                    })
                    .catch(error => {
                        console.error("Error al aprobar pago:", error);
                        Swal.fire({
                            title: "Error",
                            text: "No se pudo conectar con el servidor.",
                            icon: "error",
                            confirmButtonColor: "#dc3545"
                        });
                    });
        }
    });
}


document.addEventListener("DOMContentLoaded", function () {
    cargarHistorialVentas();
});

function cargarHistorialVentas() {
    fetch("/Mari_Store/PagoController?action=historialVentas")
            .then(response => response.json())
            .then(data => {
                const tbody = document.getElementById("tbody-historial-ventas");
                tbody.innerHTML = "";

                if (!data.success || !data.data || data.data.length === 0) {
                    tbody.innerHTML = `
                    <tr>
                        <td colspan="8" class="text-center text-muted">
                            No hay ventas registradas
                        </td>
                    </tr>
                `;
                    return;
                }

                data.data.forEach(venta => {
                    tbody.innerHTML += `
                    <tr>
                        <td>${venta.idPedido}</td>
                        <td>${venta.fecha}</td>
                        <td>
                            <strong>${venta.cliente}</strong>
                            <br>
                            <small class="text-muted">${venta.email}</small>
                        </td>
                        <td>
                            <small>${venta.productos || "Sin detalle"}</small>
                        </td>
                        <td>S/ ${Number(venta.total).toFixed(2)}</td>
                        <td>
                            <span class="badge bg-info text-light">
                                ${venta.metodoPago || "Sin pago"}
                            </span>
                            <br>
                            <small>S/ ${Number(venta.monto || 0).toFixed(2)}</small>
                        </td>
                        <td>
                            ${badgeEstado(venta.estado)}
                        </td>
                        <td>
                            ${
                            venta.comprobante
                            ? `
                                    <button class="btn btn-outline-primary btn-sm"
                                            onclick="verComprobante('${normalizarRutaComprobante(venta.comprobante)}')">
                                        <i class="bi bi-image me-1"></i> Ver
                                    </button>
                                  `
                            : `<span class="text-muted">Sin comprobante</span>`
                            }
                        </td>
                    </tr>
                `;
                });
            })
            .catch(error => {
                console.error("Error historial ventas:", error);

                document.getElementById("tbody-historial-ventas").innerHTML = `
                <tr>
                    <td colspan="8" class="text-center text-danger">
                        Error al cargar historial de ventas
                    </td>
                </tr>
            `;
            });
}

function badgeEstado(estado) {
    if (estado === "PROCESADO") {
        return `<span class="badge bg-success">Procesado</span>`;
    }

    if (estado === "PENDIENTE") {
        return `<span class="badge bg-warning text-dark">Pendiente</span>`;
    }

    if (estado === "CANCELADO") {
        return `<span class="badge bg-danger">Cancelado</span>`;
    }

    return `<span class="badge bg-secondary">${estado || "Sin estado"}</span>`;
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
        Swal.fire("Aviso", "Este pago no tiene comprobante registrado", "warning");
        return;
    }

    document.getElementById("imagen-comprobante").src = rutaImagen;

    const modal = bootstrap.Modal.getOrCreateInstance(
            document.getElementById("modalComprobante")
            );

    modal.show();
}

document.getElementById("modalComprobante").addEventListener("hidden.bs.modal", function () {
    document.getElementById("imagen-comprobante").src = "";
});
