Cálculo implícito binomial (explicação rápida):
Em slots os giros são independentes. A probabilidade real de uma combinação específica em uma payline é derivada do paytable + quantidade de símbolos por rolo. Usamos distribuição binomial pra calcular P(X = k acertos em n spins) quando a probabilidade de “acerto na payline” é implícita (p).
P(X = k) = C(n, k) * p^k * (1-p)^(n-k)

Onde p é calculado implicitamente a partir da estrutura do slot (não do RNG seed — isso é o “implícito”).
Volatilidade entra como variância alta/baixa dessa distribuição. RTP = soma (payout × probabilidade) sobre todos outcomes possíveis.


