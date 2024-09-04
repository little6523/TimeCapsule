const recordButton = document.getElementById('record-button');

let constraints;
let chunks;
let mediaRecorder;

if (navigator.mediaDevices) {

    constraints = {audio: true, video: false};
    chunks = [];

    navigator.mediaDevices.getUserMedia(constraints).then((stream) => {
        mediaRecorder = new MediaRecorder(stream);

        mediaRecorder.ondataavailable = (e) => {
            chunks.push(e.data);
        };

        mediaRecorder.onstop = () => {
            console.log("Recording has stopped.");
            const blob = new Blob(chunks, {type: 'audio/ogg; codecs=opus'});
            chunks = [];

            const file = new File([blob], "recording.ogg", {
                type: 'audio/ogg',
                lastModified: new Date().getTime()
            });

            // 기존 파일 유지 및 녹음된 파일 추가
            const dataTransfer = new DataTransfer();
            let currentFiles = Array.from(fileInput.files);
            currentFiles.forEach(f => dataTransfer.items.add(f));
            dataTransfer.items.add(file);
            fileInput.files = dataTransfer.files;

            // 업로드된 파일 이름을 표시 및 삭제 버튼 추가
            renderFileList(Array.from(dataTransfer.files));
            uploadContainer.classList.remove("active");
        };
    }).catch((err) => {
        console.error("The following error occurred: " + err);
    });
} else {
    console.error("getUserMedia is not supported in this browser.");
}

// 녹음 버튼 및 정비 버튼 스위칭, 녹음 메소드
recordButton.addEventListener('click', function () {
    if (mediaRecorder.state !== 'recording') {
        this.style.backgroundImage = 'url(../images/AfterRecording.png)';
        document.getElementById('record-text').innerText = '녹음 중...'
        mediaRecorder.start();
        console.log(mediaRecorder.state);
        console.log("recorder started");
    } else {
        this.style.backgroundImage = 'url(../images/BeforeRecording.png)';
        document.getElementById('record-text').innerText = '음성 녹음을 시작하려면 버튼을 누르세요.'
        mediaRecorder.stop();
        console.log(mediaRecorder.state);
        console.log("recorder stopped");
    }
});