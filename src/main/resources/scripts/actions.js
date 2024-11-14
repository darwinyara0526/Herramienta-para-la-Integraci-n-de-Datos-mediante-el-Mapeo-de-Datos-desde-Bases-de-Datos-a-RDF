document.addEventListener("DOMContentLoaded", function () {
    // Obtener los botones y el formulario
    const registerButton = document.querySelector(".register-button");
    const guestButton = document.querySelector(".guest-button");
    const loginForm = document.getElementById("loginForm");
    const recordPassword = document.querySelector(".forgot-password");

    // Evento de clic para redirigir al formulario de registro
    registerButton.addEventListener("click", function () {
        window.location.href = "../html/registrar_login.html"; // Redirige a registrar.html
    });

    // Evento de clic para redirigir a la interfaz de invitado
    guestButton.addEventListener("click", function () {
        window.location.href = "../html/integration_data_interface_guest.html"; // Redirige a la interfaz de invitado
    });
    
    recordPassword.addEventListener("click", function () {
        window.location.href = "../html/forgot_password.html"; // Redirige a la interfaz de invitado
    }); 
    
    // Evento de envío para el formulario de inicio de sesión
    loginForm.addEventListener("submit", function (event) {
        event.preventDefault(); // Evita el envío predeterminado del formulario

        // Captura los valores del formulario
        const email = document.getElementById("email").value;
        const password = document.getElementById("password").value;

        // Validar entrada de correo y contraseña
        if (!email || !password) {
            alert("Por favor, completa todos los campos.");
            return;
        }

        // Aquí podrías agregar más validaciones de formato de correo si lo deseas
        // Validación básica de formato de correo
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        if (!emailPattern.test(email)) {
            alert("Por favor, ingresa un correo electrónico válido.");
            return;
        }

        // Realizar autenticación (ejemplo de redirección para usuarios válidos)
        // Aquí podrías incluir lógica de autenticación contra un servidor
        if (email === "users@gmail.com" && password === "1234") {
            // Si el usuario es admin, redirige a la interfaz de administrador
            window.location.href = "../html/integration_data_interface_admin.html";
        } else {
            // Redirige a la interfaz de usuario general en caso de datos de invitado
            alert("Credenciales incorrectas. Intenta nuevamente.");
        }
    });
});
