const observador = new IntersectionObserver((entradas) => {
    for (const entrada of entradas) {
        if (!entrada.isIntersecting) continue;
        entrada.target.classList.add('dentro');
        observador.unobserve(entrada.target); // revela uma vez e esquece
    }
}, {threshold: 0.15, rootMargin: '0px 0px -10% 0px'});

document.querySelectorAll('[data-revela]').forEach(el => observador.observe(el))
// "A day with Jiggie!": o HTML já mostra o dia completo; aqui recua-se até onde o scroll vai.
// p vai de 0 (topo da secção no topo do ecrã) a 1 (fundo da secção no fundo do ecrã)
const dia = document.querySelector('.day')
const reduzirMovimento = window.matchMedia('(prefers-reduced-motion: reduce)').matches

if (dia && !reduzirMovimento) {
    const momentos = dia.querySelectorAll('.beat')
    const registos = dia.querySelectorAll('.entry')
    const relogios = dia.querySelectorAll('[data-hora]')
    const horas = ['7:20 AM', '8:05 AM', '1:40 PM', '6:00 PM']

    const limitar = (v, min, max) => Math.min(max, Math.max(min, v))
    const suavizar = t => 1 - Math.pow(1 - limitar(t, 0, 1), 3) // ease-out, como no design

    function desenhar(p) {
        // quanto já entrou cada momento (0 a 1): o 1.º entre p = 0.04 e 0.30, os seguintes 0.2 depois
        const entrada = horas.map((_, i) => suavizar((p - 0.04 - i * 0.2) / 0.26))
        const feitos = entrada.filter(v => v > 0.5).length
        const agora = limitar(feitos - 1, 0, horas.length - 1)

        momentos.forEach((momento, i) => {
            momento.classList.toggle('is-off', entrada[i] <= 0.35)
            momento.classList.toggle('is-now', entrada[i] > 0.35 && i === agora)
        })

        registos.forEach((registo, i) => {
            registo.style.setProperty('--lp', entrada[i].toFixed(3))
            registo.style.setProperty('--above', entrada.slice(i + 1).filter(v => v > 0.5).length)
        })

        relogios.forEach(el => { el.textContent = horas[agora] })
        dia.querySelector('[data-registos]').textContent =
            feitos === 0 ? 'ready to log' : `${feitos} ${feitos === 1 ? 'entry' : 'entries'}`
        dia.querySelector('[data-passeio]').textContent = Math.round(30 * entrada[0])
        dia.querySelector('[data-agua]').textContent = Math.round(250 * entrada[2])
        dia.querySelector('[data-tomas]').textContent = `${entrada[3] > 0.5 ? 1 : 0}/2`

        // ficam no .day e são herdadas por tudo o que está lá dentro
        dia.style.setProperty('--p', p.toFixed(3))
        dia.style.setProperty('--rail', suavizar(p).toFixed(3))
        dia.style.setProperty('--live', (0.35 + 0.65 * Math.abs(Math.sin(p * 12))).toFixed(3))
        dia.style.setProperty('--fab', (1 + 0.12 * Math.max(0, Math.sin(p * Math.PI * 5))).toFixed(3))
    }

    // junta os eventos de scroll e resize num só cálculo por frame
    let pedido = null
    function aoFazerScroll() {
        if (pedido) return
        pedido = requestAnimationFrame(() => {
            pedido = null
            const caixa = dia.getBoundingClientRect()
            const percurso = caixa.height - window.innerHeight
            if (percurso > 0) desenhar(limitar(-caixa.top / percurso, 0, 1))
        })
    }

    dia.classList.add('is-animated')
    window.addEventListener('scroll', aoFazerScroll, { passive: true })
    window.addEventListener('resize', aoFazerScroll)
    aoFazerScroll()
}
