<template>
  <div class="container">
    <h2>프로필 이미지 업로드 & 조회</h2>

    <!-- 파일 업로드 -->
    <input type="file" @change="handleFileUpload">
    <button @click="uploadProfile">파일 업로드</button>

    <h3>업로드한 이미지</h3>
    <img v-if="uploadedImage" :src="uploadedImage" alt="업로드된 이미지" class="image-preview">

    <hr>

    <!-- 내 정보 조회 -->
    <button @click="getProfile">내 정보 조회</button>

    <h3>조회한 프로필 이미지</h3>
    <img v-if="profileImage" :src="profileImage" alt="프로필 이미지" class="image-preview">

    <hr>

    <!-- 다른 사용자 프로필 조회 -->
    <input type="text" v-model="userId" placeholder="조회할 유저 ID 입력">
    <button @click="getOtherProfile">다른 사용자 조회</button>

    <h3>다른 사용자 프로필 이미지</h3>
    <img v-if="otherProfileImage" :src="otherProfileImage" alt="다른 사용자 프로필 이미지" class="image-preview">
    
  </div>
</template>

<script>
import { ref } from "vue";

export default {
setup() {
  const file = ref(null);
  const uploadedImage = ref(null);
  const profileImage = ref(null);
  const userId = ref("");
  const otherProfileImage = ref(null);

  const handleFileUpload = (event) => {
    const selectedFile = event.target.files[0];
    if (!selectedFile) return;

    file.value = selectedFile;
    const reader = new FileReader();
    reader.readAsDataURL(selectedFile);
    reader.onload = () => {
      uploadedImage.value = reader.result;
    };
  };

  const uploadProfile = async () => {
    if (!file.value) {
      alert("파일을 선택하세요!");
      return;
    }

    const token = localStorage.getItem("token");
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }
    const base64Data = uploadedImage.value.split(",")[1];
    const requestData = { profileImage: base64Data };

    try {
      const response = await fetch("http://localhost:8080/api/users/me", {
        method: "PUT",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
        body: JSON.stringify(requestData),
      });

      if (!response.ok) throw new Error("업로드 실패!");
      alert("파일 업로드 성공!");
    } catch (error) {
      console.error("업로드 실패:", error);
      alert("파일 업로드 실패!");
    }
  };

  const getProfile = async () => {
    const token = localStorage.getItem("token");
    if (!token) {
      alert("로그인이 필요합니다.");
      return;
    }

    try {
      const response = await fetch("http://localhost:8080/api/users/me", {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          "Authorization": `Bearer ${token}`,
        },
      });

      if (!response.ok) throw new Error("조회 실패!");
      const data = await response.json();
      profileImage.value = data.profileImageUrl || null;
    } catch (error) {
      console.error("조회 실패:", error);
      alert("내 정보 조회 실패!");
    }
  };

  const getOtherProfile = async () => {
    if (!userId.value) {
      alert("유저 ID를 입력하세요!");
      return;
    }

    try {
      const response = await fetch(`http://localhost:8080/api/users/${userId.value}/profile`, {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
        },
      });

      if (!response.ok) throw new Error("조회 실패!");

      const data = await response.json();
      otherProfileImage.value = data.profileImageUrl || null;
    } catch (error) {
      console.error("조회 실패:", error);
      alert("사용자 프로필 조회 실패!");
    }
  };

  return {
    file,
    uploadedImage,
    profileImage,
    userId,
    otherProfileImage,
    handleFileUpload,
    uploadProfile,
    getProfile,
    getOtherProfile,
  };
},
};
</script>

<style>
.container {
max-width: 500px;
margin: auto;
text-align: center;
}

.image-preview {
max-width: 300px;
margin-top: 10px;
border: 1px solid #ddd;
padding: 5px;
}
</style>
