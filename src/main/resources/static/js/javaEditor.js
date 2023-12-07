
//////////////////////////////////// 폴더 생성 //////////////////////////////////////
// 폴더명 입력 문자만 입력 가능하도록 처리
    function validateInput(input) {
        const validChars = /^[가-힣a-zA-Z0-9]+$/; // 한글, 영어, 숫자만 허용하는 정규 표현식

        // 입력된 값이 유효한 문자만 포함하고 있는지 검사합니다.
        if (!validChars.test(input.value)) {
            alert("한글, 영어, 숫자만 입력할 수 있습니다.");
            input.value = input.value.replace(/[^가-힣a-zA-Z0-9]/g, ''); // 유효하지 않은 문자 제거
        }
    }
    // 선택한 항목이 디렉토리라면 해당하는 디렉토리 하위에 디렉토리를 생성하고, 파일이면 그 파일이있는 경로에 디렉토리를 생성
    document.getElementById("mkdir2").addEventListener("click", function() {
        saveTreeState();
        let mkdirnameInput = document.getElementById("mkdirname");

        // validateInput 함수를 사용하여 입력 검증
        validateInput(mkdirnameInput);

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
        }).catch((error) => {
            console.log(error);
        });
    });






/////////////////////////////////////// 자바 코드 실행 ////////////////////////////////////////
    function send_compiler() {
    console.log(monaco_test.getValue())

    $.ajax({
    type: "POST",
    url: "/compile",
    data: JSON.stringify({ "code": monaco_test.getValue() }),
    contentType: "application/json",
    success: function(response) {
    $("#output").html(response.output.replace(/\n/g, "<br>"));
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
    function setEditor(inputValue, inputLanguage){
    return {
    value: inputValue,
    language: inputLanguage,    // 언어
    fontFamily: "D2Coding",
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
    lineHeight: 19
    }
    }

    /*<![CDATA[*/
    let monaco_test = new monaco.editor.create(document.getElementById('monaco'), setEditor("", ""));
    $('#monaco').height((monaco_test.getModel().getLineCount() * 19) + 10); // 19 = 줄 높이, 10 = 세로 스크롤 높이
    /*]]>*/

/////////////////////////////////////// 에디터 언어 변경  ///////////////////////////////////////////
    function changeLanguage() {
    var selectBox = document.getElementById('language');
    var selectedValue = selectBox.options[selectBox.selectedIndex].value;
    console.log(selectedValue);

    // 언어를 변경하기 전에 에디터의 현재 내용을 가져와서 저장
    var currentContent = monaco_test.getValue();

    // 에디터를 제거하고 새로운 언어로 다시 생성
    monaco_test.dispose();
    monaco_test = new monaco.editor.create(document.getElementById('monaco'), setEditor(currentContent, selectedValue));

    // 새로운 에디터로 기존 내용을 설정
    monaco_test.setValue(currentContent);

    // 기존 내용을 설정한 후에는 언어를 변경하도록 호출
    monaco_test.setLanguage(selectedValue);
    }



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
        console.log(fileList.children);



        // 새로운 파일 목록으로 트리뷰 재구성
        $('#tree').tree({
            primaryKey: 'id',
            uiLibrary: 'materialdesign',
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
    function transformToTreeViewFormat(fileNode) {
    var treeData = [];
    convertNode(fileNode, treeData, 1); // 재귀적으로 노드를 변환합니다.
    return treeData;
    }

    // FileNode 객체를 트리뷰 노드로 변환
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

        treeArea.addEventListener('click', function(event) {
            if (event.target.closest('a')) {
                event.preventDefault();
            }
        });
        treeArea.addEventListener('dblclick', function(event) {
            const anchor = event.target.closest('a');
            if (anchor) {
                const filename = anchor.getAttribute('href'); // 파일명 추출
                console.log("filename")
                console.log(filename)
                axios.post('/api/test2', null, {
                    params: { filename2: filename }
                })
                    .then(response => {
                        const anchorTags = treeArea.querySelectorAll('a');

                        anchorTags.forEach(anchor => {
                            anchor.style.fontWeight = 'normal';
                        });
                        const fileContent = response.data;
                        monaco_test.setValue(fileContent); // 에디터에 내용 설정
                        anchor.style.fontWeight = 'bold';
                    })
                    .catch(error => console.error('Error fetching file content:', error));
            }
        });
        treeArea.addEventListener('dblclick', function(event) {
            const anchor = event.target.closest('a');
            if (anchor) {
                const wrapper = anchor.closest('[data-role="wrapper"]');
                const expander = wrapper.querySelector('[data-role="expander"]');
                const childUl = wrapper.parentElement.querySelector('ul');

                if (expander && childUl) {
                    const currentMode = expander.getAttribute('data-mode');
                    if (currentMode === 'open') {
                        expander.setAttribute('data-mode', 'close');
                        childUl.style.display = 'none';
                    } else {
                        expander.setAttribute('data-mode', 'open');
                        childUl.style.display = 'block';
                    }
                }
            }
        });

    }

/////////////////////////////////////// ZIP 파일로 다운로드 ////////////////////////////////////////
    document.getElementById('saveZip').addEventListener('click', function() {
        window.location.href = '/java/download-zip';
    });

    Split(['#left-pane', '#right-pane'], {
    sizes: [25, 75],
    minSize: 200
});
