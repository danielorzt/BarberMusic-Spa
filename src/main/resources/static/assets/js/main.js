/**
* Template Name: iLanding
* Template URL: https://bootstrapmade.com/ilanding-bootstrap-landing-page-template/
* Updated: Oct 28 2024 with Bootstrap v5.3.3
* Author: BootstrapMade.com
* License: https://bootstrapmade.com/license/
*/

(function() {
	"use strict";

	/**
	 * Apply .scrolled class to the body as the page is scrolled down
	 */
	function toggleScrolled() {
		const selectBody = document.querySelector('body');
		const selectHeader = document.querySelector('#header');

		if (selectHeader && selectBody) {
			if (!selectHeader.classList.contains('scroll-up-sticky') &&
				!selectHeader.classList.contains('sticky-top') &&
				!selectHeader.classList.contains('fixed-top')) return;

			window.scrollY > 100 ? selectBody.classList.add('scrolled') : selectBody.classList.remove('scrolled');
		}
	}

	if (document.querySelector('#header')) {
		document.addEventListener('scroll', toggleScrolled);
		window.addEventListener('load', toggleScrolled);
	}

	/**
	 * Mobile nav toggle
	 */
	const mobileNavToggleBtn = document.querySelector('.mobile-nav-toggle');

	function mobileNavToogle() {
		const body = document.querySelector('body');
		if (body && mobileNavToggleBtn) {
			body.classList.toggle('mobile-nav-active');
			mobileNavToggleBtn.classList.toggle('bi-list');
			mobileNavToggleBtn.classList.toggle('bi-x');
		}
	}

	if (mobileNavToggleBtn) {
		mobileNavToggleBtn.addEventListener('click', mobileNavToogle);
	}

	/**
	 * Hide mobile nav on same-page/hash links
	 */
	const navmenuLinks = document.querySelectorAll('#navmenu a');
	if (navmenuLinks.length > 0) {
		navmenuLinks.forEach(navmenu => {
			navmenu.addEventListener('click', () => {
				if (document.querySelector('.mobile-nav-active')) {
					mobileNavToogle();
				}
			});
		});
	}

	/**
	 * Toggle mobile nav dropdowns
	 */
	const toggleDropdowns = document.querySelectorAll('.navmenu .toggle-dropdown');
	if (toggleDropdowns.length > 0) {
		toggleDropdowns.forEach(navmenu => {
			navmenu.addEventListener('click', function(e) {
				e.preventDefault();
				if (this.parentNode && this.parentNode.nextElementSibling) {
					this.parentNode.classList.toggle('active');
					this.parentNode.nextElementSibling.classList.toggle('dropdown-active');
					e.stopImmediatePropagation();
				}
			});
		});
	}

	/**
	 * Scroll top button
	 */
	let scrollTop = document.querySelector('.scroll-top');

	function toggleScrollTop() {
		if (scrollTop) {
			window.scrollY > 100 ? scrollTop.classList.add('active') : scrollTop.classList.remove('active');
		}
	}

	if (scrollTop) {
		scrollTop.addEventListener('click', (e) => {
			e.preventDefault();
			window.scrollTo({
				top: 0,
				behavior: 'smooth'
			});
		});

		window.addEventListener('load', toggleScrollTop);
		document.addEventListener('scroll', toggleScrollTop);
	}

	/**
	 * Animation on scroll function and init
	 */
	function aosInit() {
		if (typeof AOS !== 'undefined') {
			AOS.init({
				duration: 600,
				easing: 'ease-in-out',
				once: true,
				mirror: false
			});
		}
	}
	window.addEventListener('load', aosInit);

	/**
	 * Initiate glightbox
	 */
	if (typeof GLightbox !== 'undefined') {
		const glightbox = GLightbox({
			selector: '.glightbox'
		});
	}

	/**
	 * Init swiper sliders
	 */
	function initSwiper() {
		if (typeof Swiper !== 'undefined') {
			document.querySelectorAll(".init-swiper").forEach(function(swiperElement) {
				const configElement = swiperElement.querySelector(".swiper-config");
				if (configElement) {
					let config = JSON.parse(configElement.innerHTML.trim());

					if (swiperElement.classList.contains("swiper-tab")) {
						initSwiperWithCustomPagination(swiperElement, config);
					} else {
						new Swiper(swiperElement, config);
					}
				}
			});
		}
	}

	window.addEventListener("load", initSwiper);

	/**
	 * Initiate Pure Counter
	 */
	if (typeof PureCounter !== 'undefined') {
		new PureCounter();
	}

	/**
	 * Frequently Asked Questions Toggle
	 */
	const faqItems = document.querySelectorAll('.faq-item h3, .faq-item .faq-toggle');
	if (faqItems.length > 0) {
		faqItems.forEach((faqItem) => {
			faqItem.addEventListener('click', () => {
				if (faqItem.parentNode) {
					faqItem.parentNode.classList.toggle('faq-active');
				}
			});
		});
	}

	/**
	 * Correct scrolling position upon page load for URLs containing hash links.
	 */
	window.addEventListener('load', function(e) {
		if (window.location.hash) {
			const hashSection = document.querySelector(window.location.hash);
			if (hashSection) {
				setTimeout(() => {
					let scrollMarginTop = getComputedStyle(hashSection).scrollMarginTop;
					window.scrollTo({
						top: hashSection.offsetTop - parseInt(scrollMarginTop),
						behavior: 'smooth'
					});
				}, 100);
			}
		}
	});

	/**
	 * Navmenu Scrollspy
	 */
	let navmenulinks = document.querySelectorAll('.navmenu a');

	function navmenuScrollspy() {
		navmenulinks.forEach(navmenulink => {
			if (!navmenulink.hash) return;
			let section = document.querySelector(navmenulink.hash);
			if (!section) return;
			let position = window.scrollY + 200;
			if (position >= section.offsetTop && position <= (section.offsetTop + section.offsetHeight)) {
				document.querySelectorAll('.navmenu a.active').forEach(link => link.classList.remove('active'));
				navmenulink.classList.add('active');
			} else {
				navmenulink.classList.remove('active');
			}
		});
	}

	if (navmenulinks.length > 0) {
		window.addEventListener('load', navmenuScrollspy);
		document.addEventListener('scroll', navmenuScrollspy);
	}

	/**
	 * Appointment Date Validation
	 */
	const appointmentInput = document.getElementById("appointment");
	if (appointmentInput) {
		// Obtener la fecha de mañana en el formato adecuado para datetime-local
		let tomorrow = new Date();
		tomorrow.setDate(tomorrow.getDate() + 1);
		let year = tomorrow.getFullYear();
		let month = String(tomorrow.getMonth() + 1).padStart(2, "0");
		let day = String(tomorrow.getDate()).padStart(2, "0");
		let minHour = "08:00"; // Hora mínima de selección (puedes cambiarla)

		// Formato YYYY-MM-DDTHH:MM para datetime-local
		let minDateTime = `${year}-${month}-${day}T${minHour}`;

		// Configurar la fecha mínima en el input
		appointmentInput.min = minDateTime;
	}

	/**
	 * Chatbot Functionality
	 */
	const chatContainer = document.getElementById('chatbot-container');
	const floatBtn = document.getElementById('chatbot-float-btn');
	const chatInput = document.getElementById('chatbot-input');
	const chatSend = document.getElementById('chatbot-send');
	const chatMessages = document.getElementById('chatbot-messages');
	const chatToggle = document.getElementById('chatbot-toggle');

	if (chatContainer && floatBtn && chatInput && chatSend && chatMessages && chatToggle) {
		// Verificar si es la primera vez usando localStorage
		const hasChatbotOpenedBefore = localStorage.getItem('chatbotOpened');

		// Mostrar el chatbot después de 5 segundos solo si es la primera vez
		if (!hasChatbotOpenedBefore) {
			setTimeout(() => {
				chatContainer.classList.add('active');
				// Marcar en localStorage que ya se mostró
				localStorage.setItem('chatbotOpened', 'true');
			}, 5000);
		}

		// Toggle del chatbot - cerrar
		chatToggle.addEventListener('click', function() {
			chatContainer.classList.remove('active');
		});

		// Botón flotante - abrir
		floatBtn.addEventListener('click', function() {
			chatContainer.classList.add('active');
		});

		// Resto de la lógica del chat (envío de mensajes)
		chatSend.addEventListener('click', sendMessage);
		chatInput.addEventListener('keypress', function(e) {
			if (e.key === 'Enter') sendMessage();
		});

		function sendMessage() {
			const message = chatInput.value.trim();
			if (!message) return;

			addMessage('user', message);
			chatInput.value = '';

			const typingIndicator = addMessage('assistant', '...');
			typingIndicator.classList.add('typing');

			// Simular respuesta o conectar con backend
			setTimeout(() => {
				chatMessages.removeChild(typingIndicator);
				addMessage('assistant', 'Gracias por tu mensaje. ¿En qué puedo ayudarte?');
			}, 1000);
		}

		function addMessage(sender, text) {
			const messageDiv = document.createElement('div');
			messageDiv.className = `chat-message ${sender}`;
			messageDiv.textContent = text;
			chatMessages.appendChild(messageDiv);
			chatMessages.scrollTop = chatMessages.scrollHeight;
			return messageDiv;
		}

	}

	/**
	 * Custom Slider for Services & Sucursales
	 */
	const customSliderContainer = document.querySelector('.slider-container');
	const sliderTrack = document.querySelector('.slider-track');
	const sliderDots = document.querySelectorAll('.slider-dot');
	
	if (customSliderContainer && sliderTrack && sliderDots.length > 0) {
		let currentSlide = 0;
		const totalSlides = sliderDots.length;
		let isAnimating = false;
		let autoSlideInterval;

		// Initialize slider
		function initSlider() {
			updateSlider();
			startAutoSlide();
			
			// Add touch/swipe support
			addTouchSupport();
			
			// Add keyboard support
			document.addEventListener('keydown', handleKeyPress);
		}

		// Update slider position and active dot
		function updateSlider() {
			if (isAnimating) return;
			
			isAnimating = true;
			const translateX = -currentSlide * 100;
			sliderTrack.style.transform = `translateX(${translateX}%)`;
			
			// Update active dot
			sliderDots.forEach((dot, index) => {
				dot.classList.toggle('active', index === currentSlide);
			});
			
			// Reset animation flag after transition
			setTimeout(() => {
				isAnimating = false;
			}, 600);
		}

		// Go to specific slide
		function goToSlide(slideIndex) {
			if (slideIndex >= 0 && slideIndex < totalSlides && slideIndex !== currentSlide) {
				currentSlide = slideIndex;
				updateSlider();
			}
		}

		// Go to next slide
		function nextSlide() {
			const nextIndex = (currentSlide + 1) % totalSlides;
			goToSlide(nextIndex);
		}

		// Go to previous slide
		function prevSlide() {
			const prevIndex = (currentSlide - 1 + totalSlides) % totalSlides;
			goToSlide(prevIndex);
		}

		// Auto slide functionality
		function startAutoSlide() {
			autoSlideInterval = setInterval(nextSlide, 5000); // 5 seconds
		}

		function stopAutoSlide() {
			if (autoSlideInterval) {
				clearInterval(autoSlideInterval);
			}
		}

		function restartAutoSlide() {
			stopAutoSlide();
			startAutoSlide();
		}

		// Touch/swipe support
		function addTouchSupport() {
			let startX = 0;
			let startY = 0;
			let isSwipe = false;

			customSliderContainer.addEventListener('touchstart', (e) => {
				startX = e.touches[0].clientX;
				startY = e.touches[0].clientY;
				isSwipe = false;
				stopAutoSlide();
			});

			customSliderContainer.addEventListener('touchmove', (e) => {
				if (!startX || !startY) return;
				
				const deltaX = Math.abs(e.touches[0].clientX - startX);
				const deltaY = Math.abs(e.touches[0].clientY - startY);
				
				if (deltaX > deltaY && deltaX > 30) {
					isSwipe = true;
					e.preventDefault();
				}
			});

			customSliderContainer.addEventListener('touchend', (e) => {
				if (!isSwipe || !startX) return;
				
				const endX = e.changedTouches[0].clientX;
				const diffX = startX - endX;
				
				if (Math.abs(diffX) > 50) {
					if (diffX > 0) {
						nextSlide();
					} else {
						prevSlide();
					}
				}
				
				startX = 0;
				startY = 0;
				isSwipe = false;
				restartAutoSlide();
			});
		}

		// Keyboard navigation
		function handleKeyPress(e) {
			if (!customSliderContainer.matches(':hover')) return;
			
			switch(e.key) {
				case 'ArrowLeft':
					e.preventDefault();
					prevSlide();
					restartAutoSlide();
					break;
				case 'ArrowRight':
					e.preventDefault();
					nextSlide();
					restartAutoSlide();
					break;
			}
		}

		// Dot navigation
		sliderDots.forEach((dot, index) => {
			dot.addEventListener('click', () => {
				goToSlide(index);
				restartAutoSlide();
			});
		});

		// Pause auto-slide on hover
		customSliderContainer.addEventListener('mouseenter', stopAutoSlide);
		customSliderContainer.addEventListener('mouseleave', startAutoSlide);

		// Initialize the slider
		initSlider();
	}

	/**
	 * Login Form Toggle Animation
	 */
	const loginContainer = document.getElementById('containerL');
	const registerBtn = document.getElementById('register');
	const loginBtn = document.getElementById('login');

	if (loginContainer && registerBtn && loginBtn) {
		registerBtn.addEventListener('click', function(e) {
			e.preventDefault();
			loginContainer.classList.add("active");
			console.log('Switched to register form');
		});

		loginBtn.addEventListener('click', function(e) {
			e.preventDefault();
			loginContainer.classList.remove("active");
			console.log('Switched to login form');
		});
		
		console.log('Login toggle functionality initialized');
	}

})();