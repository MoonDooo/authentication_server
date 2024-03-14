# Spring Security를 이용한 OAuth2 인증 및 JWT 발급

Spring security을 활용하여 [travel plan](https://github.com/IF-TG/spring/wiki) 프로젝트에 필요한 사용자 로그인 및 인증을 구현한 프로젝트입니다. 사용자의 회원 가입 및 로그인은 OAUTH2를 이용합니다. OAUTH2는 흔히 볼 수 있는 소셜 로그인으로서 사용자에게 편리하게 가입할 수 있도록 지원합니다. 이 프로젝트는 현재 naver를 이용한 로그인을 지원하고 있습니다. 사용자는 OAUTH2을 통해 회원 가입 후 JWT를 발급 받으며 해당 JWT를 관리하기 위한 블랙 리스트, 갱신 등을 구현했습니다. 

### 블로그(https://moondooo.tistory.com/3)

### OAUTH2LOGIN

<details>
 <summary>Spring security OAUTH2 LOGIN 구조</summary>

![image](https://github.com/MoonDooo/authentication_server/assets/47065431/0cccc13a-d102-40b0-b873-d3a72ae57f34)

</details> 

### Spring Security OAuth2 Login

스프링 시큐리티는 굉장히 쉽게 OAUTH2를 구현할 수 있도록 되어있다. 다만 요구사항으로 IOS와 통신하기 때문에 REST API에 맞게 구현할 필요가 있었다. 스프링 시큐리티는 사용자가 인증해야 하는 uri를 생성하여 리다이렉트한다. 또한 해당 사용자의 요청 정보를 임시적으로 저장하는 곳에서 세션을 사용한다. 따라서 해당 부분을 적절히 커스텀했다. 

 - 자세히 https://github.com/MoonDooo/authentication_server/wiki/Spring-Security-OAuth2-Login

### JWT 발급

OAuth2 인증이 끝나면 사용자 정보를 데이터베이스에 저장해 회원가입하고 JWT를 발급한다. 접근 토큰과 리프레시 토큰을 발급하고 있으며, 리프레시 토큰까지 만료되면 다시 OAuth2 인증이 필요하다. 발급된 jwt는 `travel_plan` 서비스에서 `Header`에 넣어 요청하게 되며 `travel_plan`의 `JwtAuthenticationFilter`를 통해 이 프로젝트의 서버로 jwt를 전송하여 검증 받고 `sub` 즉 데이터베이스 상의 `user` id를 얻는다.
 
 - 자세히  https://github.com/MoonDooo/authentication_server/wiki/JWT-%EB%B0%9C%EA%B8%89

### JWT 인증 및 관리

발급된 JWT는 `travel_plan`에서 사용되어 이 프로젝트 서버로 `restTemplate`를 사용하여 전달 받는다. 전달 받은 JWT는 `JwtAuthenticationFilter`에서 검증하여 데이터베이스 상의 id를 가져온다. 이때 `travel_plan` 프로젝트의 서버에 해당 user의 id가 데이터베이스에 없다면 id를 저장한다. 즉 `travel_plan`은 `authentication_server`에서의 user에 id만을 저장하며 사용자로부터 추가적으로 `nickname`과 `profileImg`만 저장한다. 또한 로그아웃 기능을 지원하기 위해서 jwt 블랙리스트 필터인 `JwtBlackListFilter`와 토큰을 갱신하기 위한 `JwtRefreshFilter`를 필터로 추가했다.

 - **인증** https://github.com/MoonDooo/authentication_server/wiki/JWT-%EC%9D%B8%EC%A6%9D
 - **갱신&삭제** https://github.com/MoonDooo/authentication_server/wiki/JWT-%EB%B8%94%EB%9E%99%EB%A6%AC%EC%8A%A4%ED%8A%B8-%EB%B0%8F-%EA%B0%B1%EC%8B%A0
