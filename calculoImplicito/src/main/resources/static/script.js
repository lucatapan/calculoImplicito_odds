document.addEventListener('DOMContentLoaded', () => {

    function showLoading(elementId) {
        const el = document.getElementById(elementId);
        if (el) el.textContent = 'Calculando...';
    }

    function showError(elementId) {
        const el = document.getElementById(elementId);
        if (el) el.textContent = 'Erro ao conectar com o servidor';
    }

    // Binomial
    document.getElementById('binomial-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        showLoading('result-binomial');
        const n = document.getElementById('n').value;
        const k = document.getElementById('k').value;

        try {
            const res = await fetch(`http://localhost:8080/binomial/${n}/${k}`);
            const data = await res.json();
            document.getElementById('result-binomial').innerHTML = 
                `<strong>${data.result}</strong><br><br>` +
                `<span style="color:#555; font-size:0.95em;">${data.explanation}</span>`;
        } catch (err) {
            showError('result-binomial');
        }
    });

    // Probabilidade Binomial
    document.getElementById('prob-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        showLoading('result-prob');
        const n = document.getElementById('n-prob').value;
        const k = document.getElementById('k-prob').value;
        const pPercent = document.getElementById('p').value;
        const p = parseFloat(pPercent) / 100;

        try {
            const res = await fetch(`http://localhost:8080/binomial/prob/${n}/${k}/${p}`);
            const data = await res.json();
            document.getElementById('result-prob').innerHTML = 
                `<strong>${data.result}</strong><br><br>` +
                `<span style="color:#555; font-size:0.95em;">${data.explanation}</span>`;
        } catch (err) {
            showError('result-prob');
        }
    });

    // Martingale
    document.getElementById('martingale-form').addEventListener('submit', async (e) => {
        e.preventDefault();
        const resultEl = document.getElementById('result-martingale');
        resultEl.textContent = 'Simulando... (pode demorar em rodadas altas)';

        const bankroll = document.getElementById('bankroll').value;
        const aposta = document.getElementById('aposta').value;
        const pPercent = document.getElementById('p-mart').value;
        const p = parseFloat(pPercent) / 100;
        const rodadas = document.getElementById('rodadas').value;

        try {
            const url = `http://localhost:8080/martingale/simulate?bankrollInicial=${bankroll}&apostaInicial=${aposta}&p=${p}&maxRodadas=${rodadas}`;
            const res = await fetch(url);
            if (!res.ok) throw new Error('Erro na simulação');
            const texto = await res.text();
            resultEl.textContent = texto + '\n\nAVISO: Em jogos reais com edge da casa (p < 0.5), a quebra é quase inevitável a longo prazo.';
        } catch (err) {
            resultEl.textContent = 'Erro ao simular Martingale';
        }
    });
});

function limpar() {
    document.getElementById('result-binomial').innerHTML = '';
    document.getElementById('result-prob').innerHTML = '';
    document.getElementById('result-martingale').textContent = '';
}

function loadHistory() {
    fetch('http://localhost:8080/simulations')
        .then(response => response.json())
        .then(data => {
            const list = document.getElementById('history-list');
            list.innerHTML = '';
            data.forEach(sim => {
                const li = document.createElement('li');
                li.style.margin = '10px 0';
                li.style.padding = '10px';
                li.style.background = '#f1f1f1';
                li.style.borderRadius = '6px';
                li.innerHTML = `
                    <strong>${sim.type.toUpperCase()}</strong> - ${sim.timestamp}<br>
                    n=${sim.n}, k=${sim.k}${sim.p ? `, p=${(sim.p * 100).toFixed(1)}%` : ''}<br>
                    Resultado: ${sim.result}
                `;
                list.appendChild(li);
            });
        })
        .catch(err => {
            document.getElementById('history-list').innerHTML = '<li>Erro ao carregar histórico</li>';
        });
}

function clearHistory() {
    if (!confirm("Tem certeza que quer limpar TODO o histórico?")) return;

    fetch('http://localhost:8080/simulations/clear', { method: 'DELETE' })
        .then(response => response.text())
        .then(texto => {
            alert(texto);
            document.getElementById('history-list').innerHTML = '';
            loadHistory();
        })
        .catch(err => alert('Erro ao limpar histórico'));
}