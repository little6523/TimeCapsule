const uploadContainer = document.querySelector("#upload-container");
const fileInput = document.getElementById('file-upload');
const uploadText = document.getElementById('upload-text');

// 드래그한 파일 객체가 해당 영역에 놓였을 때
uploadContainer.ondrop = (e) => {
    e.preventDefault();

    // 파일 리스트
    const files = [...e.dataTransfer?.files];
    console.log(files);

    // file 타입의 input 요소에 drop한 파일 전달
    fileInput.files = e.dataTransfer.files;

    uploadText.innerText = files.map(v => v.name).join("<br>");
    uploadContainer.classList.remove("active");
}

// ondragover 이벤트가 없으면 onDrop 이벤트가 실행되지 않습니다.
uploadContainer.ondragover = (e) => {
    e.preventDefault();
}

// 드래그한 파일이 최초로 진입했을 때
uploadContainer.ondragenter = (e) => {
    e.preventDefault();

    uploadContainer.classList.add("active");
}

// 드래그한 파일이 영역을 벗어났을 때
uploadContainer.ondragleave = (e) => {
    e.preventDefault();

    uploadContainer.classList.remove("active");
}