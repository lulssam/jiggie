const observador = new IntersectionObserver((entradas) => {
    for (const entrada of entradas) {
        if (!entrada.isIntersecting) continue;
        entrada.target.classList.add('dentro');
        observador.unobserve(entrada.target); // revela uma vez e esquece
    }
}, {threshold: 0.15, rootMargin: '0px 0px -10% 0px'});

document.querySelectorAll('[data-revela]').forEach(el => observador.observe(el))