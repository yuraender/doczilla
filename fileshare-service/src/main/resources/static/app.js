const form = document.getElementById('uploadForm');
const progressWrap = document.getElementById('progressWrap');

form.addEventListener('submit', e => {
    e.preventDefault();
    const file = document.getElementById('fileInput').files[0];
    if (!file) {
        return alert('Выберите файл');
    }

    const data = new FormData();
    data.append('file', file);

    const request = new XMLHttpRequest();
    request.open('POST', '/upload');
    request.onload = function () {
        progressWrap.classList.add('hidden');
        if (request.status !== 200) {
            showError('Ошибка загрузки');
            return;
        }
        const json = JSON.parse(request.responseText);
        const link = document.getElementById('downloadLink');
        link.href = json.link;
        link.textContent = window.location.origin + json.link;
        document.getElementById('result').classList.remove('hidden');
    };
    request.upload.onprogress = function (e) {
        progressWrap.classList.remove('hidden');
        const progress = Math.round(e.loaded / e.total * 100);
        document.getElementById('progressBar').style.width = progress + '%';
        document.getElementById('progressText').textContent = progress + '%';
    };
    request.onerror = () => showError('Ошибка сети');
    request.send(data);
});


function showError(msg) {
    const el = document.getElementById('error');
    el.textContent = msg;
    el.classList.remove('hidden');
}
