const $drop = document.querySelector("#upload-container");
const fileInput = document.getElementById('file-upload');
const uploadText = document.getElementById('upload-text');

// 드래그한 파일 객체가 해당 영역에 놓였을 때
$drop.ondrop = (e) => {
    e.preventDefault();

    // 파일 리스트
    const files = [...e.dataTransfer?.files];
    console.log(files);

    // file 타입의 input 요소에 drop한 파일 전달
    fileInput.files = e.dataTransfer.files;

    uploadText.innerText = files.map(v => v.name).join("<br>");
    $drop.classList.remove("active");
}

// ondragover 이벤트가 없으면 onDrop 이벤트가 실행되지 않습니다.
$drop.ondragover = (e) => {
    e.preventDefault();
}

// 드래그한 파일이 최초로 진입했을 때
$drop.ondragenter = (e) => {
    e.preventDefault();

    $drop.classList.add("active");
}

// 드래그한 파일이 영역을 벗어났을 때
$drop.ondragleave = (e) => {
    e.preventDefault();

    $drop.classList.remove("active");
}

const recordButton = document.getElementById('record-button');
recordButton.addEventListener('click', function () {

    const computedStyle = window.getComputedStyle(this);
    const backgroundImage = computedStyle.backgroundImage;

    console.log(backgroundImage + ' 클릭되었습니다!')

    if (backgroundImage.includes('BeforeRecording.png')) {
        this.style.backgroundImage = 'url(../images/AfterRecording.png)';
        document.getElementById('record-text').innerText = '녹음 중...'
    } else {
        this.style.backgroundImage = 'url(../images/BeforeRecording.png)';
        document.getElementById('record-text').innerText = '음성 녹음을 시작하려면 버튼을 누르세요.'
    }
});