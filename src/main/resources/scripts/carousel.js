let currentSlide = 0;
const slides = document.querySelectorAll(".carousel-slide");
const indicatorsContainer = document.querySelector(".carousel-indicators");

// Mostrar la diapositiva actual y resaltar el indicador correspondiente
function showSlide(index) {
    slides.forEach((slide, i) => {
        slide.style.display = i === index ? "block" : "none";
        indicatorsContainer.children[i].classList.toggle("active", i === index);
    });
}

// Función para ir a la diapositiva anterior
function prevSlide() {
    currentSlide = (currentSlide > 0) ? currentSlide - 1 : slides.length - 1;
    showSlide(currentSlide);
}

// Función para ir a la siguiente diapositiva
function nextSlide() {
    currentSlide = (currentSlide < slides.length - 1) ? currentSlide + 1 : 0;
    showSlide(currentSlide);
}

// Cambio automático de diapositivas cada 5 segundos
setInterval(() => {
    nextSlide();
}, 5000);

// Inicialización de indicadores y muestra de la primera diapositiva
document.addEventListener("DOMContentLoaded", () => {
    slides.forEach((_, i) => {
        const indicator = document.createElement("span");
        indicator.classList.add("indicator");
        indicator.addEventListener("click", () => {
            currentSlide = i;
            showSlide(currentSlide);
        });
        indicatorsContainer.appendChild(indicator);
    });
    showSlide(currentSlide);
});
