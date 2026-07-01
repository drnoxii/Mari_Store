function agregarVariante(talla = "", color = "", stock = "") {
            const contenedor = document.getElementById("contenedor-variantes");

            const fila = document.createElement("div");
            fila.className = "row align-items-end mb-2 variante-item";

            fila.innerHTML = `
            <div class="col-md-4">
                <label class="form-label">Talla</label>
                <input type="text" name="talla" class="form-control" value="${talla}" placeholder="S, M, L, 38..." required>
            </div>

            <div class="col-md-4">
                <label class="form-label">Color</label>
                <input type="text" name="color" class="form-control" value="${color}" placeholder="Negro, Blanco..." required>
            </div>

            <div class="col-md-3">
                <label class="form-label">Stock</label>
                <input type="number" name="stock" class="form-control" value="${stock}" min="0" required>
            </div>

            <div class="col-md-1">
                <button type="button" class="btn btn-danger w-100" onclick="eliminarVariante(this)">
                    <i class="bi bi-trash"></i>
                </button>
            </div>
        `;

            contenedor.appendChild(fila);
        }

function eliminarVariante(boton) {
            const variantes = document.querySelectorAll(".variante-item");

            if (variantes.length <= 1) {
                alert("Debe existir al menos una variante");
                return;
            }

            boton.closest(".variante-item").remove();
        }

        function limpiarVariantes() {
            document.getElementById("contenedor-variantes").innerHTML = "";
        }
    
        document.addEventListener("DOMContentLoaded", function () {
            cargarContadorPendientes();
        });

        function cargarContadorPendientes() {
            fetch("/Mari_Store/PagoController?action=listarPagosPendientes")
                    .then(response => response.json())
                    .then(data => {
                        if (data.success) {
                            document.getElementById("contador-pendientes").textContent = data.cantidad;
                        } else {
                            document.getElementById("contador-pendientes").textContent = 0;
                        }
                    })
                    .catch(error => {
                        console.error("Error al cargar contador:", error);
                        document.getElementById("contador-pendientes").textContent = 0;
                    });
        }
