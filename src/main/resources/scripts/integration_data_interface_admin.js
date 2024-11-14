// Redirecciona a la página de inicio
function volver() {
    window.location.href = "../html/index.html";
}

// Redirecciona a la página de conexión de base de datos
function agregarBaseDatos() {
    window.location.href = "../html/connect_database.html";
}

// Redirecciona a la página de administración de usuario
function administrarUsuario() {
    window.location.href = "../html/admin_users.html";
}

// Muestra las bases de datos conectadas
function mostrarBasesDatosConectadas() {
    const connectedDbList = document.getElementById("connected-db-list");
    const databases = ["MySQL", "Oracle", "PostgreSQL"]; // Lista de bases de datos

    // Limpia el contenido de la lista
    connectedDbList.innerHTML = "";

    // Agrega cada base de datos como un elemento de bloque arrastrable
    databases.forEach((db, index) => {
        const dbItem = document.createElement("div");
        dbItem.className = "db-item";
        dbItem.textContent = db;
        dbItem.setAttribute("draggable", "true");
        dbItem.setAttribute("id", `db-item-${index}`);
        dbItem.ondragstart = drag;
        connectedDbList.appendChild(dbItem);
    });
}

// Permitir que el elemento se arrastre
function allowDrop(event) {
    event.preventDefault();
}

// Maneja el evento de arrastrar
function drag(event) {
    event.dataTransfer.setData("text", event.target.id);
}

// Maneja el evento de soltar
function drop(event) {
    event.preventDefault();
    const data = event.dataTransfer.getData("text");
    const draggedElement = document.getElementById(data);
    event.target.appendChild(draggedElement);
}

// Inicializa el proceso de integración
function iniciarIntegracion() {
    const statusMessage = document.getElementById("status-message");
    statusMessage.textContent = "Integrando las bases de datos...";
    statusMessage.classList.add("info-message");

    // Simulación del proceso de integración
    setTimeout(() => {
        statusMessage.textContent = "Integración completada con éxito.";
        document.getElementById("export-options").classList.remove("hidden");
    }, 2000);
}

// Ejecuta mostrarBasesDatosConectadas al cargar la página
document.addEventListener("DOMContentLoaded", mostrarBasesDatosConectadas);
