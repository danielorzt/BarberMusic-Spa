/**
 * ==============================================
 * Simple Theme Toggle Logic for BarberSpa
 * ==============================================
 * 
 * Handles theme switching (light/dark) by toggling a 'data-theme' 
 * attribute on the <html> element and persists the choice in localStorage.
 * All color logic is handled by the SCSS/CSS files.
 */

(function() {
    'use strict';

    const themeToggleButton = document.getElementById('theme-toggle');
    if (!themeToggleButton) {
        return;
    }
    
    const sunIcon = themeToggleButton.querySelector('.bi-sun');
    const moonIcon = themeToggleButton.querySelector('.bi-moon-stars');
    const htmlElement = document.documentElement;

    /**
     * Applies the saved theme and updates icon visibility.
     * @param {string} theme - The theme to apply ('dark' or 'light').
     */
    const applyTheme = (theme) => {
        if (theme === 'dark') {
            htmlElement.setAttribute('data-theme', 'dark');
            if (sunIcon) sunIcon.style.display = 'block';
            if (moonIcon) moonIcon.style.display = 'none';
        } else {
            htmlElement.removeAttribute('data-theme');
            if (sunIcon) sunIcon.style.display = 'none';
            if (moonIcon) moonIcon.style.display = 'block';
        }
    };

    /**
     * Toggles the current theme, saves it, and applies it.
     */
    const toggleTheme = () => {
        const currentTheme = htmlElement.getAttribute('data-theme') ? 'dark' : 'light';
        const newTheme = currentTheme === 'dark' ? 'light' : 'dark';
        
        localStorage.setItem('barberspa-theme', newTheme);
        applyTheme(newTheme);
    };

    // Attach event listener
    themeToggleButton.addEventListener('click', toggleTheme);

    // --- Initial Theme Load ---
    // 1. Check for a saved theme in localStorage.
    // 2. If not found, check for the user's OS preference.
    // 3. Default to 'light' theme if nothing is set.
    const savedTheme = localStorage.getItem('barberspa-theme');
    const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;

    const initialTheme = savedTheme || (prefersDark ? 'dark' : 'light');
    applyTheme(initialTheme);

})();

console.log('🎨 theme.js cargado correctamente');