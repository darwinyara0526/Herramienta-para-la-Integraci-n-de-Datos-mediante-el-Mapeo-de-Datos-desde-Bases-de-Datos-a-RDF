function volver() {
    window.location.href = "../html/admin_users.html"; // Redirige al archivo admin_user.html
}

function login() {
    alert("Hola admi, pasando a configuraciones de admi...");
}

function agregarUsuario(event) {
    event.preventDefault();
    const email = document.getElementById("email").value;
    const password = document.getElementById("password").value;
    const description = document.getElementById("description").value;

    alert(`Usuario agregado:\nCorreo: ${email}\nDescripción: ${description}`);
    
    // Aquí podrías añadir el código para enviar estos datos a tu servidor o base de datos
}
