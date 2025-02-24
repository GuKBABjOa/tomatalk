import { ref } from 'vue'
import { defineStore } from 'pinia'

export const useSavedItemsStore = defineStore('savedItems', () => {
  const savedItems = ref<any[]>([]) // 메모 저장 배열

  function addMemo(memo: any) {
    savedItems.value.unshift(memo) // 새로운 메모 추가
  }

  function addResource(resource: any) {
    savedItems.value.unshift(resource);
  }

  function removeMemo(id: string) {
    savedItems.value = savedItems.value.filter((item) => item.id !== id) // 메모 삭제
  }

  return { savedItems, addMemo, addResource, removeMemo }
}, {
  persist: true // 새로고침해도 데이터 유지됨
})
