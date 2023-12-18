//////////////////////////////////// Split //////////////////////////////////////
Split(['#left-pane', '#center-pane', '#right-pane'], {
    sizes: [20, 50,30],
    minSize: [0, 200, 0]
});


//////////////////////////////////// 폴더 생성 //////////////////////////////////////
// 폴더명 입력 문자만 입력 가능하도록 처리
    function validateInput(input) {
        const validChars = /^[가-힣a-zA-Z0-9]+$/; // 한글, 영어, 숫자만 허용하는 정규 표현식

        // 입력된 값이 유효한 문자만 포함하고 있는지 검사합니다.
        if (!validChars.test(input.value)) {
            alert("한글, 영어, 숫자만 입력할 수 있습니다.");
            return false;
            // input.value = input.value.replace(/[^가-힣a-zA-Z0-9]/g, ''); // 유효하지 않은 문자 제거
        }
    }
    // 선택한 항목이 디렉토리라면 해당하는 디렉토리 하위에 디렉토리를 생성하고, 파일이면 그 파일이있는 경로에 디렉토리를 생성
    document.getElementById("mkdir2").addEventListener("click", function() {
        saveTreeState();
        let mkdirnameInput = document.getElementById("mkdirname");

        // validateInput 함수를 사용하여 입력 검증
        if (!validateInput(mkdirnameInput)) {
            this.blur();
            return false;
        }

        const selectedElement = document.querySelector('[data-selected="true"]');

        var anchor;
        var href;
        if (selectedElement) {
            // data-id 값을 추출
            const dataId = selectedElement.getAttribute('data-id');

            // 선택된 요소 내부의 a 태그
            anchor = selectedElement.querySelector('a');
            href = anchor ? anchor.getAttribute('href') : null;

            console.log('Data ID:', dataId);
            console.log('Href:', href);
        } else {
            console.log('선택된 요소가 없습니다.');

            var mid = document.getElementById("user_mid").value;
            href = '/'+mid
            console.log('mid : '+href);
        }

        //작성한 폴더명
        let mkdirname = document.getElementById("mkdirname").value;
        //현재 선택된 경로
        let path = href;
        let dir = { 'mkdirname': mkdirname, 'path': path };
        axios.post("/api/mkdir", dir).then((response) => {
            // 저장 후 파일 목록 다시 불러오기
            $('#tree').remove(); // 트리를 완전히 제거합니다.
            loadFileList();
            modal.style.display = 'none';

        }).catch((error) => {
            console.log(error);
        });
    });

/////////////////////////////////////// 자바 코드 실행 ////////////////////////////////////////
    function send_compiler() {
    console.log(monaco_test.getValue())


        $("#run").blur();
    $("#output").html("<div style='text-align: center; width: 100%'><div class='spinner-border text-secondary' role='status' >\n" +
        // "  <span class='sr-only' style='text-align: center'>Loading...</span>\n" +
        "</div></div>");

    $.ajax({
    type: "POST",
    url: "/compile",
    data: JSON.stringify({ "code": monaco_test.getValue() }),
    contentType: "application/json",
    success: function(response) {
    $("#output").html(response.output.replace(/\n/g, "<br>"));
    $("#compileTime").html("<i class=\"fa-solid fa-circle\" style=\"color: #1b4134; font-size: 10px; margin-right: 6px;\"></i>"+response.time + ","+response.date );
},
    error: function(error) {
    $("#output").html("Error: " + error.responseText);
}
});
}



    // 클래스 이름이 "file_name"인 모든 요소를 선택합니다.
    // const fileListContainer = document.querySelector('#fileListContainer');
    // const filename2Input = document.getElementById('filename2');
    // const filename3Input = document.getElementById('downloadName');
    // fileListContainer.addEventListener('dblclick', (event) => {
    //     const targetElement = event.target;
    //     if (targetElement.classList.contains('file_name')) {
    //         // 파일 경로에서 파일명만 추출하여 filename2Input에 설정
    //         const filePath = targetElement.textContent;
    //         const fileName = filePath.split('/').pop();
    //         filename2Input.value = fileName;
    //         filename3Input.value = fileName;
    //
    //         let filename2 = document.getElementById("filename2").value;
    //         console.log(filename2)
    //
    //         axios.post("/api/test2", null, {
    //             params: {
    //                 filename2: filename2
    //             }
    //         })  // 객체 형태로 전달
    //             .then((response) => {
    //                 console.log(response.data);
    //                 monaco_test.setValue(response.data.toString());// Monaco 에디터 내용 설정
    //             })
    //             .catch((error) => {
    //                 console.log(error);
    //             });
    //     }
    // });



/////////////////////////////////////// 지정한 파일명으로 파일 저장  ////////////////////////////////////////
    document.getElementById("saveBtn").addEventListener("click", () => {
    var viewLinesElements = document.getElementsByClassName("view-line");
    var allTextValues = [];

    for (var i = 0; i < viewLinesElements.length; i++) {
    var viewLinesElement = viewLinesElements[i];
    var textValue = viewLinesElement.textContent;
    allTextValues.push(textValue);
}
    var textToSave = allTextValues.join('\n');

    var blob = new Blob([textToSave], { type: "text/plain" });

    var a = document.createElement("a");
    a.style.display = "none";
    a.href = window.URL.createObjectURL(blob);
    a.download = document.getElementById('downloadName').value; // 사용자가 원하는 파일명 지정

    document.body.appendChild(a);
    a.click();

    window.URL.revokeObjectURL(a.href);
    document.body.removeChild(a);
});

/////////////////////////////////////// 처음 에디터 생성  ////////////////////////////////////////
    function setEditor(inputValue){
    return {
    value: inputValue,      // 에디터 내용 설정
    language: "java",    // 언어
    fontSize: 16,
    theme: "vs-dark",   // 테마
    lineNumbers: 'on',  // 줄 번호
    glyphMargin: false, // 체크 이미지 넣을 공간이 생김
    vertical: 'auto',
    horizontal: 'auto',
    verticalScrollbarSize: 10,
    horizontalScrollbarSize: 10,
    scrollBeyondLastLine: false, // 에디터상에서 스크롤이 가능하게
    readOnly: false,    // 수정 가능 여부
    automaticLayout: true, // 부모 div 크기에 맞춰서 자동으로 editor 크기 맞춰줌
    minimap: {
    enabled: true // 우측 스크롤 미니맵
    },
    lineHeight: 20,

    }
    }

    /*<![CDATA[*/
    let monaco_test = new monaco.editor.create(document.getElementById('monaco'), setEditor(""));
    $('#monaco').height((monaco_test.getModel().getLineCount() * 19) + 10); // 19 = 줄 높이, 10 = 세로 스크롤 높이
    /*]]>*/


/////////////////////////////////////// 파일 트리 구조 설정  ///////////////////////////////////////////
    let savedState = [];

    function saveTreeState() {
    savedState = [];
    $('#tree').find('[data-role="node"]').each(function() {
    let expander = $(this).find('[data-role="expander"]');
    if (expander.attr('data-mode') === 'open') {
    savedState.push($(this).attr('data-id'));
    }
    });
    }
    function restoreTreeState() {
    $('#tree').find('[data-role="node"]').each(function() {
        let node = $(this);
        let id = node.attr('data-id');

        if (savedState.includes(id)) {
            node.find('[data-role="expander"]').attr('data-mode', 'open').find('i.gj-icon').removeClass('chevron-right').addClass('chevron-down');
            node.children('ul').show();
        } else {
            node.find('[data-role="expander"]').attr('data-mode', 'close').find('i.gj-icon').removeClass('chevron-down').addClass('chevron-right');
            node.children('ul').hide();
        }
        });
    }
    // 페이지 로드 시에 실행되도록
    document.addEventListener('DOMContentLoaded', function() {
    // 초기 파일 목록 불러오기
    loadFileList();
    });




    // 저장 버튼 클릭 시 파일을 저장하고 목록을 다시 불러옴
    document.getElementById("btn1").addEventListener("click", function() {
    saveTreeState();
    let filename = document.getElementById("filename").value;
    let monaco =  monaco_test.getValue();
    let memo = { 'filename': filename, 'monaco': monaco };
    axios.post("/api/test1", memo).then((response) => {
    // 저장 후 파일 목록 다시 불러오기
    $('#tree').remove(); // 트리를 완전히 제거합니다.
    loadFileList();
    }).catch((error) => {
        console.log(error);
    });
    });

    function loadFileList() {
    axios.get('/java/fileList').then(response => {
        const fileList = response.data;
        if ($('#left-pane').find('#tree').length === 0) {
            $('#left-pane').append('<div id="tree"></div>');
        }
        console.log(fileList);



        // 새로운 파일 목록으로 트리뷰 재구성
        $('#tree').tree({
            primaryKey: 'id',
            uiLibrary: 'materialdesign',
            // dataSource: transformToTreeViewFormat(fileList),
            dataSource: transformToTreeViewFormat(fileList),

            imageUrlField: 'flagUrl'
        });

        // 트리 재구성 후 상태 복원
        restoreTreeState();
        treeEvent();
        }).catch(error => {
            console.error('Error fetching file list:', error);
        });
    }

    // FileNode 객체를 트리뷰 형식으로 변환
function transformToTreeViewFormat(fileList) {
    var treeData = [];
    fileList.forEach(function(fileNode) {
        convertNode(fileNode, treeData, 1);
    });
    return treeData;
}

function convertNode(fileNode, treeData, nodeId) {
    var node = {
        id: nodeId,
        text: "<a href='" + fileNode.text + "'>" + fileNode.name + "</a>",
        flagUrl: fileNode.flagUrl,
        children: []
    };

    fileNode.children.forEach(function(child) {
        convertNode(child, node.children, nodeId + 1);
        nodeId++;
    });

    treeData.push(node);
}

// document.addEventListener('DOMContentLoaded', function() {
    //     const treeArea = document.querySelector('#tree');
    //
    //     treeArea.addEventListener('click', function(event) {
    //         if (event.target.tagName === 'A') {
    //             event.preventDefault(); // 클릭에 의한 기본 동작(링크 이동)을 방지합니다.
    //         }
    //     });
    //
    //     treeArea.addEventListener('dblclick', function(event) {
    //         if (event.target.tagName === 'A') {
    //             console.log('더블클릭된 링크:', event.target.href); // 콘솔에 링크 출력
    //             // 더블클릭에 대한 추가적인 동작을 여기에 구현합니다.
    //         }
    //
    //     });
    // });
    // document.addEventListener('DOMContentLoaded', function() {
    function treeEvent() {
        const treeArea = document.querySelector('#tree');

//////////////////////////// a 태그 이동 막기 /////////////////////////////
        treeArea.addEventListener('click', function(event) {
            if (event.target.closest('a')) {
                event.preventDefault();
            }
        });

/////////////////////////////////////// 더블클릭해서 파일 내용 불러오기 ////////////////////////////////////////////
        treeArea.addEventListener('dblclick', function(event) {
            const anchor = event.target.closest('a');
            if (anchor) {
                const filepath = anchor.getAttribute('href'); // 파일명 추출
                const filename = anchor.textContent;
                console.log("filename")
                console.log(filepath)
                console.log(filename)
                axios.post('/api/test2', null, {
                    params: { filename2: filepath }
                })
                    .then(response => {
                        const anchorTags = treeArea.querySelectorAll('a');

                        anchorTags.forEach(anchor => {
                            anchor.style.fontWeight = 'normal';
                        });
                        const fileContent = response.data;
                        monaco_test.setValue(fileContent); // 에디터에 내용 설정
                        document.getElementById('selectedFileName').textContent = filename;
                        document.getElementById('selectedFileName').title = removeFirstSegment(filepath);

                        anchor.style.fontWeight = 'bold';
                    })
                    .catch(error => console.error('Error fetching file content:', error));
            }
        });

//////////////////////////////////// 더블클릭해서 폴더 열고 닫기  /////////////////////////////////////////
        treeArea.addEventListener('dblclick', function(event) {
            const anchor = event.target.closest('a');
            if (anchor) {
                const wrapper = anchor.closest('[data-role="wrapper"]');
                const expander = wrapper.querySelector('[data-role="expander"]');
                const image = wrapper.querySelector('[data-role="image"] img');
                const icon = expander.querySelector('i');
                const childUl = wrapper.parentElement.querySelector('ul');

                if (expander && childUl) {
                    const currentMode = expander.getAttribute('data-mode');
                    if (currentMode === 'open') {
                        expander.setAttribute('data-mode', 'close');
                        childUl.style.display = 'none';
                        image.src = '/static/img/icon/folder.svg';
                        icon.classList.remove('chevron-down');
                        icon.classList.add('chevron-right');

                    } else {
                        expander.setAttribute('data-mode', 'open');
                        childUl.style.display = 'block';
                        image.src = '/static/img/icon/folder_open_FILL0_wght400_GRAD0_opsz24.svg';
                        icon.classList.remove('chevron-right');
                        icon.classList.add('chevron-down');

                    }
                }
            }
        });

    }

function removeFirstSegment(path) {
    const segments = path.split('/');
    segments.splice(1, 1);
    return  segments.join('/');
}

/////////////////////////////////////// 새파일 추가 ////////////////////////////////////////
document.getElementById("newFile2").addEventListener("click", function (){
    saveTreeState();
    let newFile = document.getElementById("newFile").value;
    let newMonaco =  "";
    let memo = { 'filename': newFile, 'monaco': newMonaco };
    axios.post("/api/newFile", memo).then((response) => {
        // 저장 후 파일 목록 다시 불러오기
        $('#tree').remove(); // 트리를 완전히 제거합니다.
        loadFileList();
        fileload(newFile);
        modal2.style.display = 'none';
    }).catch((error) => {
        console.log(error);
    });
});

function fileload(filename) {
    axios.post('/api/test2', null, {
        params: { filename2: filename }
    }).then((response) => {
            const fileContent = response.data;
            monaco_test.setValue(fileContent); // 에디터에 내용 설정
            document.getElementById('selectedFileName').textContent = filename;
            document.getElementById('selectedFileName').title = removeFirstSegment(filename);
            console.log("여기까쥐!");
        })
        .catch((error) => {
            console.error('Error fetching file content:', error);
        });
}


/////////////////////////////////////// 모달창 및 사이드메뉴 ////////////////////////////////////////

const btn = document.getElementById('popupBtn');
const modal = document.getElementById('modalWrap');
const closeBtn = document.getElementById('closeBtn');

const btn2 = document.getElementById('popupBtn2');
const modal2 = document.getElementById('modalWrap2');
const closeBtn2 = document.getElementById('closeBtn2');

const btn3 = document.getElementById('popupBtn3');
const modal3 = document.getElementById('modalWrap3');
const closeBtn3 = document.getElementById('closeBtn3');

const icon = document.getElementById('iconNav');
const balloon = document.getElementById('balloon');

const icon2 = document.getElementById('moreNav');
const balloon2 = document.getElementById('balloon2');

btn.onclick = function() {
    modal.style.display = 'block';
}
closeBtn.onclick = function() {
    modal.style.display = 'none';
}

window.onclick = function(event) {
    if (event.target == modal || event.target == modal2 || event.target == model3) {
        modal.style.display = "none";
        modal2.style.display = "none";
        modal3.style.display = "none";
    }
}

btn2.onclick = function() {
    modal2.style.display = 'block';
}
closeBtn2.onclick = function() {
    modal2.style.display = 'none';
}

btn3.onclick = function() {
    modal3.style.display = 'block';
}
closeBtn3.onclick = function() {
    modal3.style.display = 'none';
}


icon.addEventListener('mouseenter', function (){
    balloon.style.display = 'block';
})

icon.addEventListener('mouseover', function (){
    console.log("bye");
    balloon.style.display = 'block';
})

icon.addEventListener('mouseout', function (){
    balloon.style.display = 'none';
})


icon2.addEventListener('mouseenter', function (){
    balloon2.style.display = 'block';
})

icon2.addEventListener('mouseover', function (){
    balloon2.style.display = 'block';
})

icon2.addEventListener('mouseout', function (){
    balloon2.style.display = 'none';
})


/////////////////////////////////////// ZIP 파일로 다운로드 ////////////////////////////////////////
document.getElementById('saveZip').addEventListener('click', function() {
    window.location.href = '/java/download-zip';
});



