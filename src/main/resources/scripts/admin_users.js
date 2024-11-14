function allowDrop(ev) {
    ev.preventDefault();
}

function drag(ev) {
    ev.dataTransfer.setData("text", ev.target.id);
}

function dropPermission(ev) {
    ev.preventDefault();
    var data = ev.dataTransfer.getData("text");
    var dbName = document.getElementById(data).innerText;

    // Crear un nuevo bloque para el permiso otorgado
    var newPermissionBlock = document.createElement("div");
    newPermissionBlock.innerText = dbName;
    newPermissionBlock.className = "db-block-permission";
    newPermissionBlock.draggable = true;
    newPermissionBlock.ondragstart = drag;
    newPermissionBlock.ondrop = dropPermission;
    newPermissionBlock.ondragover = allowDrop;

    // Agregar el nuevo bloque al área de permisos otorgados
    ev.target.appendChild(newPermissionBlock);
}

function agregarUsuario() {
    window.location.href = "../html/add_user.html";
}

function volver() {
   window.location.href = "../html/integration_data_interface_admin.html";
}

function login() {
    alert("Hola admin, pasando a configuraciones de admin...");
}
