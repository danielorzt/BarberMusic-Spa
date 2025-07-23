const sideMenu = document.querySelector('aside');
const menuBtn = document.getElementById('menu-btn');
const closeBtn = document.getElementById('close-btn');
const darkMode = document.querySelector('.dark-mode');

// Inicializar modo oscuro desde localStorage al cargar la página
document.addEventListener('DOMContentLoaded', () => {
	// Verificar si hay una preferencia guardada
	const isDarkModeAdmin = localStorage.getItem('darkMode') === 'true';

	// Aplicar tema según la preferencia guardada
	document.body.classList.toggle('dark-mode-variables', isDarkModeAdmin);

	// Activar el icono correspondiente
	const lightModeIcon = darkMode.querySelector('span:nth-child(1)');
	const darkModeIcon = darkMode.querySelector('span:nth-child(2)');

	if (lightModeIcon && darkModeIcon) {
		lightModeIcon.classList.toggle('active', !isDarkModeAdmin);
		darkModeIcon.classList.toggle('active', isDarkModeAdmin);
	}
});

// Toggle sidebar
menuBtn.addEventListener('click', () => sideMenu.classList.add('active'));
closeBtn.addEventListener('click', () => sideMenu.classList.remove('active'));

// Toggle dark mode
darkMode.addEventListener('click', () => {
	document.body.classList.toggle('dark-mode-variables');

	const lightModeIcon = darkMode.querySelector('span:nth-child(1)');
	const darkModeIcon = darkMode.querySelector('span:nth-child(2)');

	if (lightModeIcon && darkModeIcon) {
		lightModeIcon.classList.toggle('active');
		darkModeIcon.classList.toggle('active');
	}

	// Guardar preferencia en localStorage
	localStorage.setItem('darkMode', document.body.classList.contains('dark-mode-variables'));
});

// Función para marcar el enlace activo en el sidebar
function setActiveLink(linkId) {
	const link = document.getElementById(linkId);
	if (link) {
		// Remover active de todos los enlaces primero
		document.querySelectorAll('aside .sidebar a').forEach(item => {
			item.classList.remove('active');
		});
		// Agregar active al enlace actual
		link.classList.add('active');
	}
}

// Confirmación de eliminación mejorada
document.querySelectorAll('.btn-delete').forEach(btn => {
	btn.addEventListener('click', function(e) {
		if (!confirm('¿Estás seguro de eliminar este campo?')) {
			e.preventDefault();
		}
	});
});

function fijarRecordatorio(id, fijar) {
	fetch('/recordatorios/fijar/' + id, {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		}
	})
		.then(response => {
			if (response.ok) {
				// Recargar la página para reflejar los cambios
				location.reload();
			} else {
				alert('Error al cambiar estado del recordatorio');
			}
		})
		.catch(error => {
			console.error('Error:', error);
			alert('Error al cambiar estado del recordatorio');
		});
}

/**
 * Enhanced function to delete a reminder using SweetAlert2.
 * @param {number} id - The ID of the reminder to delete.
 */
async function eliminarRecordatorio(id) {
	const confirmed = await showConfirmDialog(
		'¿Eliminar Recordatorio?',
		'¿Estás seguro de que deseas eliminar este recordatorio?'
	);

	if (confirmed) {
		try {
			const response = await fetch(`/recordatorios/api/delete/${id}`, {
				method: 'DELETE',
				headers: {
					'Content-Type': 'application/json'
				}
			});

			if (response.ok) {
				// Find and remove the element from the UI without reloading
				const reminderElement = document.querySelector(`#reminder-${id}`);
				if (reminderElement) {
					reminderElement.remove();
				}
				showSuccessAlert('Recordatorio eliminado con éxito.');
			} else {
				showErrorAlert('Error al eliminar el recordatorio.');
			}
		} catch (error) {
			console.error('Fetch error:', error);
			showErrorAlert('Error de conexión al eliminar.');
		}
	}
}

// Activar los dropdowns
document.addEventListener('DOMContentLoaded', function () {
	const dropdownToggles = document.querySelectorAll('.dropdown-toggle');

	dropdownToggles.forEach(toggle => {
		toggle.addEventListener('click', function (e) {
			e.stopPropagation();
			const dropdown = this.nextElementSibling;

			// Cerrar todos los demás dropdowns
			document.querySelectorAll('.dropdown-menu').forEach(menu => {
				if (menu !== dropdown) {
					menu.classList.remove('show');
				}
			});

			// Alternar el estado del dropdown actual
			dropdown.classList.toggle('show');
		});
	});

	// Cerrar dropdowns al hacer clic en cualquier parte
	document.addEventListener('click', function () {
		document.querySelectorAll('.dropdown-menu').forEach(menu => {
			menu.classList.remove('show');
		});
	});
});