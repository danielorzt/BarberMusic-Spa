/**
 * ==============================================
 * SweetAlert2 Configuration for BarberSpa
 * ==============================================
 * 
 * Provides standardized, theme-aware alert functions to be used
 * across the application, replacing all native JavaScript alerts.
 */

// Detecta el tema actual para aplicar estilos de SweetAlert2 acordes
const isDarkMode = () => document.documentElement.getAttribute('data-theme') === 'dark';

// Opciones base para todos los SweetAlerts, adaptables al tema
const getSwalDefaultOptions = () => {
    const darkMode = isDarkMode();
    return {
        confirmButtonColor: darkMode ? '#c31c1c' : '#5dc1b9', // Rojo Barbería o Aguamarina
        cancelButtonColor: darkMode ? '#3c3c3c' : '#7a7a7a',
        background: darkMode ? '#2d2d2d' : '#ffffff',
        color: darkMode ? '#ffffff' : '#3c3c3c',
        iconColor: darkMode ? '#c31c1c' : '#5dc1b9',
        timer: 3000,
        timerProgressBar: true,
        toast: true,
        position: 'top-end',
        showConfirmButton: false,
    };
};

/**
 * Muestra una alerta de éxito.
 * @param {string} title - El título del mensaje.
 */
function showSuccessAlert(title) {
    Swal.fire({
        ...getSwalDefaultOptions(),
        icon: 'success',
        title: title,
    });
}

/**
 * Muestra una alerta de error.
 * @param {string} title - El título del mensaje de error.
 * @param {string} [text] - Texto adicional opcional.
 */
function showErrorAlert(title, text = '') {
    Swal.fire({
        ...getSwalDefaultOptions(),
        icon: 'error',
        title: title,
        text: text,
        toast: false, // Los errores importantes no deberían ser toasts
        position: 'center',
        timer: 5000 
    });
}

/**
 * Muestra una alerta de información.
 * @param {string} title - El título del mensaje.
 */
function showInfoAlert(title) {
    Swal.fire({
        ...getSwalDefaultOptions(),
        icon: 'info',
        title: title,
    });
}

/**
 * Muestra un diálogo de confirmación.
 * @param {string} title - El título de la pregunta de confirmación.
 * @param {string} text - El texto explicativo.
 * @returns {Promise<boolean>} - Resuelve a `true` si se confirma, `false` si se cancela.
 */
async function showConfirmDialog(title, text) {
    const result = await Swal.fire({
        ...getSwalDefaultOptions(),
        title: title,
        text: text,
        icon: 'warning',
        position: 'center',
        toast: false,
        showCancelButton: true,
        showConfirmButton: true,
        timer: null, // No auto-cierra los diálogos de confirmación
        confirmButtonText: 'Sí, continuar',
        cancelButtonText: 'Cancelar',
    });
    return result.isConfirmed;
}

/**
 * Escucha los mensajes flash desde el backend y los muestra.
 * Esta función debe ser llamada cuando el DOM esté listo.
 */
function handleFlashMessages() {
    const flashMessageContainer = document.getElementById('flash-message-container');
    if (flashMessageContainer) {
        const successMessage = flashMessageContainer.getAttribute('data-success');
        const errorMessage = flashMessageContainer.getAttribute('data-error');

        if (successMessage) {
            showSuccessAlert(successMessage);
        }
        if (errorMessage) {
            showErrorAlert(errorMessage);
        }
    }
}

// Ejecutar el manejador de mensajes flash cuando el DOM esté completamente cargado.
document.addEventListener('DOMContentLoaded', handleFlashMessages);

console.log('🍬 SweetAlert2 config cargado');