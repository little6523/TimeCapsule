const uploadContainer = document.querySelector("#upload-container");
const fileInput = document.getElementById('file-upload');
const uploadText = document.getElementById('upload-text');

// 드래그한 파일 객체가 해당 영역에 놓였을 때
uploadContainer.ondrop = (e) => {
    e.preventDefault();

    // 파일 리스트
    const files = [...e.dataTransfer?.files];
    console.log(files);

    for (let i = 0; i < files.length; i++) {
        if (!validation(files[i])) {
            uploadContainer.classList.remove("active");
            return;
        }
    }

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

// 첨부 파일 업로드 검증 메소드
function validation(obj) {
    const soundFileTypes = ['audio/mpeg', 'audio/wav', 'audio/ogg', 'audio/flac', 'audio/aac', 'audio/m4a',
        'audio/x-ms-wma', 'application/x-hwp'];
    const imageFileTypes = ['image/jpeg', 'image/png', 'image/gif', 'image/bmp', 'image/webp', 'image/tiff'];

    if (obj.name.length > 100) {
        alert("파일명이 100자 이상인 파일은 첨부할 수 없습니다.");
        return false;
    } else if (obj.size > (100 * 1024 * 1024)) {
        alert("최대 파일 용량인 100MB를 초과한 파일은 첨부할 수 없습니다.");
        return false;
    } else if (obj.name.lastIndexOf('.') === -1) {
        alert("확장자가 없는 파일은 첨부할 수 없습니다.");
        return false;
    } else if (!soundFileTypes.includes(obj.type) && !imageFileTypes.includes(obj.type)) {
        alert("사운드, 이미지 파일외의 파일은 첨부할 수 없습니다.");
        return false;
    } else {
        return true;
    }
}