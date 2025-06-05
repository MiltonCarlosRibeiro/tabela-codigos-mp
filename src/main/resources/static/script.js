async function buscarCodigo() {
    const input = document.getElementById("codigoInput");
    const codigo = input.value.trim();

    if (!codigo) {
        Swal.fire("Aviso", "Digite ou cole um código antes de buscar.", "warning");
        return;
    }

    try {
        const response = await fetch(`/api/buscar?codigo=${encodeURIComponent(codigo)}`);
        const resultado = await response.json();

        if (!resultado || resultado.length === 0) {
            Swal.fire("Nenhum resultado", "Nenhuma linha encontrada com esse código.", "info");
            return;
        }

        renderizarResultados(resultado);
    } catch (error) {
        console.error("Erro:", error);
        Swal.fire("Erro", "Falha ao buscar os dados.", "error");
    }
}

function renderizarResultados(dados) {
    const container = document.getElementById("tabela-container");
    container.innerHTML = ""; // limpa resultados anteriores

    dados.forEach((item) => {
        const card = document.createElement("div");
        card.className = "resultado-card";

        const titulo = document.createElement("h3");
        titulo.textContent = `🗂 Aba: ${item.aba}`;
        card.appendChild(titulo);

        for (const [chave, valor] of Object.entries(item.colunas)) {
            const linha = document.createElement("p");
            linha.innerHTML = `<strong>${chave}:</strong> ${valor}`;
            linha.onclick = () => copiar(valor);
            card.appendChild(linha);
        }

        container.appendChild(card);
    });
}

function copiar(texto) {
    navigator.clipboard.writeText(texto);
    Swal.fire({
        toast: true,
        position: 'top-end',
        icon: 'success',
        title: 'Texto copiado!',
        showConfirmButton: false,
        timer: 1000
    });
}

// Enter ativa busca
document.getElementById("codigoInput").addEventListener("keypress", function (e) {
    if (e.key === "Enter") buscarCodigo();
});
