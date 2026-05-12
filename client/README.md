# FE

## 💻 Code Convention

### 네이밍 룰(Naming Rules)

1. **패키지 네이밍** - 항상 소문자로 작성하고 단어 사이는 언더스코어( _ )를 사용하는 snake_case를 사용합니다. (Dart의 표준 파일 시스템 규칙입니다.)
2. 클래스 네이밍 - 대문자로 시작하며 단어의 첫 글자를 대문자로 작성하는 PascalCase를 사용합니다.

```Dart
class GameController extends ChangeNotifier {
  ...
}
```

3. 메소드, 변수 네이밍 - 소문자로 시작하며 단어 사이를 대문자로 구분하는 lowerCamelCase를 사용합니다.

```Dart
// 변수 예시
int currentLevel = 1;
String childNickname = "heewo";

// 메소드 예시
void startGame() {
  ...
}
```

4. 상수 네이밍 - Dart 공식 가이드에 따라 lowerCamelCase를 사용합니다. 전역 상수는 구분을 위해 k를 접두어로 붙이는 관습을 따릅니다.

```Dart
const kMaxGameTime = 60;
const defaultPadding = 16.0;
```

### 소스코드 구성(Source code organization)

클래스의 내용은 다음 순서로 작성합니다.

1. 속성(Fields) 선언 (final 및 일반 변수)
2. 생성자(Constructor)
3. build 메소드 (Flutter 위젯인 경우)
4. 사용자 정의 메소드 선언
5. 내부 전용(Private) 메소드 (_로 시작)

```Dart
// 클래스 예시
class AdhdGameWidget extends StatelessWidget {

    // 1. 속성 선언
    final String gameId;
    final int difficulty;

    // 2. 생성자
    const AdhdGameWidget({
        super.key, 
        required this.gameId, 
        this.difficulty = 1,
    });

    // 3. 빌드 메소드
    @override
    Widget build(BuildContext context) {
        return Container(
            child: _buildScoreBoard(), // 내부 메소드 호출
        );
    }

    // 4. 기타 메소드 선언
    void restartGame() { ... }

    // 5. 내부 전용 메소드
    Widget _buildScoreBoard() {
        return Text('Score: 0');
    }
}
```

### 형식 (Formatting)

1. 들여쓰기 - 들여쓰기에는 **두 개의 공백(2 spaces)**을 사용합니다. (Flutter는 위젯 트리가 깊어지기 때문에 2칸이 표준입니다.)
2. 콜론( : ) - Dart에서는 타입 지정이나 매개변수 이름을 쓸 때 다음 규칙을 따릅니다.
    - 선언과 해당하는 타입을 구분하는 콜론 앞에는 공백을 넣지 않습니다.
    - 콜론 뒤에는 항상 공백을 넣습니다.
    - Named Parameter(이름 있는 인자)를 전달할 때도 동일하게 적용합니다.
3. 후행 쉼표(Trailing Comma) - 함수의 마지막 매개변수나 위젯 트리 끝에 반드시 쉼표(,)를 추가합니다. 이는 안드로이드 스튜디오에서 코드를 자동 정렬할 때 가독성을 획기적으로 높여줍니다.

참고 자료: https://dart.dev/effective-dart/style

## 🛠️ Branch Strategy

### 브랜치 유형

| 브랜치 유형 | 내용 |
| --- | --- |
| `main` | 완성된 버전의 코드를 저장하는 브랜치 |
| `dev` | 개발이 진행되는 동안 완성된 코드를 저장하는 브랜치 |
| `feat` | 작은 단위의 작업이 진행되는 브랜치 |
| `hotfix` | 긴급한 오류를 해결하는 브랜치 |

### 브랜치 명

- 유형/#이슈번호-what
    
    ex) feat/#30-home-ui,  init/#1-add-font
    

| 카테고리 | 내용 |
| --- | --- |
| `feat` | 구현 |
| `mod` | 수정 |
| `add` | 추가 |
| `del` | 삭제 |
| `fix` | 버그 수정 |
| `refactor` | 리팩토링 |

## 📔 Git Convention

### Git Flow

1. Issue 생성
2. Branch 생성
3. Add - Commit - Push - Pull Request(PR)
    1. Commit은 최대한 자주, 적은 양
    2. Commit시에 Issue를 연결
4. PR이 작성되면 작성자 이외의 다른 팀원이 Code Review를 진행합니다.
5. Code Review가 완료되면 PR 작성자가 dev Branch로 Merge 합니다.
    1. Merge 후 카톡방에 무조건 공유합니다.
6. Merge 된 작업이 있으면 다른 브랜치에서 작업을 진행 중이던 개발자는 본인의 브랜치로 Merge된 작업을 Pull 받아옵니다. (최신화 습관 들이기!)

### 협업 규칙

- dev 브랜치에서의 작업은 금지합니다. 단, 초기 세팅 및 README 작성은 dev 브랜치에서 수행 가능합니다.
- 본인의 PR은 본인이 Merge합니다.
- Commit, Push, Merge, PR 등 모든 작업은 앱이 정상적으로 실행되는지 확인 후 수행합니다.

### Issue Convention

[카테고리] 제목 

ex) [INIT] 프로젝트 초기 세팅 

### Commit Convention

[커밋 카테고리/#이슈번호] 커밋 내용 (대문자)

ex) [FEAT/#30] 홈 뷰 구현, [ADD/#1] 폰트 파일 추가

| 커밋 카테고리 | 내용 |
| --- | --- |
| `feat` | 기능 (feature) |
| `fix` | 버그 수정 |
| `docs` | 문서 작업 (documentation) |
| `style` | 포맷팅, 세미콜론 누락 등, 코드 자체의 변경이 없는 경우 |
| `refactor` | 리팩토링 : 결과의 변경 없이 코드의 구조를 재조정 |
| `test` | 테스트 |
| `chore` | 변수명, 함수명 등 사소한 수정 *ex) .gitignore* |

### PR Convention

[카테고리/#이슈번호] 제목

ex) [FEAT/#6] 로그인 뷰 구현


## 📑 사용 기술 스택 및 라이브러리

| 구분 | 기술 / 라이브러리 | 설명 |
| --- | --- | --- |



## ⚙️Android Studio 환경 설정
버전 : Panda 2

targetSDK : 

minSDK : 

## ⚙️Flutter 환경 설정
버전 : 3.41.4 
