async function loadComponent(id, file) {
    const response = await fetch(file);

    if (!response.ok) {
        throw new Error("No se pudo cargar el componente: " + file + " | Status: " + response.status);
    }

    const data = await response.text();
    document.getElementById(id).innerHTML = data;
}

function loadScript(src) {
    return new Promise((resolve, reject) => {
        const script = document.createElement('script');
        script.src = src;

        script.onload = () => {
            console.log("Script cargado:", src);
            resolve();
        };

        script.onerror = () => {
            reject(new Error("No se pudo cargar el script: " + src));
        };

        document.body.appendChild(script);
    });
}

async function init() {
    try {
        await loadComponent('head-placeholder', 'head.html');
        await loadComponent('header-placeholder', 'header.html');
        await loadComponent('footer-placeholder', 'footer.html');

        await loadScript('assets/js/jquery-3.6.0.min.js');
        await loadScript('assets/js/jquery.dataTables.min.js');
        await loadScript('assets/js/dataTables.bootstrap5.min.js');
        await loadScript('assets/js/bootstrap.bundle.min.js');
        await loadScript('https://cdn.jsdelivr.net/npm/sweetalert2@11');

        // cargar los scripts y funciones

        await loadScript('assets/js/tienda.js');
        setTimeout(() => {
            if (typeof verificarSesion() === 'function'){
                verificarSesion();}
            if (typeof cargarProductos() === 'function')
                cargarProductos();
            if (typeof inicializarEventosAuth() === 'function')
                inicializarEventosAuth();
            if (typeof actualizarContadorCarrito() === 'function')
                actualizarContadorCarrito();
            if (typeof cargarCarrito() === 'function')
                cargarCarrito();
            if (typeof cargarProductos() === 'function')
                cargarProductos();
            if (typeof cargarProductosAdmin() === 'function')
                cargarProductosAdmin();
            if (typeof inicializarFiltroCategorias() === 'function')
                inicializarFiltroCategorias();
            if(typeof ojitodepass()() === 'function')
                ojitodepass()();
        }, 200);

    } catch (e) {
        console.error("Error al cargar la aplicacion:", e.message);
    }
}

init();



