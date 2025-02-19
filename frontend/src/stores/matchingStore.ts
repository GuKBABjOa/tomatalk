import { defineStore } from "pinia";

interface Participant {
  name: string;
  image: string | null;
}

interface MatchingDetails {
  format: string;
  category: string;
  subject: string;
  matchedCount: number;
  participants: Participant[];
  debateId: string | number | null;
  openviduToken: string | null;
}

export const useMatchingStore = defineStore("matching", {
  state: () => ({
    isMatching: false,
    isMatched: false,
    matchingDetails: {
      format: "",
      category: "",
      subject: "",
      matchedCount: 0,
      participants: [],
      debateId: null,
      openviduToken: null,
    } as MatchingDetails,
    matchedCount: 0,
    isExpanded: true,
  }),
  actions: {
    setFormat(format: string) {
      this.matchingDetails = { ...this.matchingDetails, format };
    },
    setCategory(category: string) {
      this.matchingDetails = { ...this.matchingDetails, category };
    },
    startMatching() {
      this.isMatching = true;
      this.isMatched = false;
      this.matchedCount = 0;
    },
    setMatchedCount(count: number) {
      this.matchedCount = count;
      this.matchingDetails.matchedCount = count;
    },
    cancelMatching() {
      this.isMatching = false;
      this.isMatched = false; // ✅ 매칭 취소 시 모달 안 뜨도록 수정
      this.clearMatching();
    },
    completeMatching(subject: string, participants: Participant[]) {
      this.isMatching = false;
      this.isMatched = true;
      this.matchingDetails.subject = subject;
      this.matchingDetails.participants = participants; // ✅ 매칭된 참가자 정보 저장
    },
    setDebateId(id: string | number) {
      this.matchingDetails.debateId = id;
    },
    setOpenviduToken(token: string) {
      this.matchingDetails.openviduToken = token;
    },
    clearMatching() {
      this.matchedCount = 0;
      this.matchingDetails = {
        format: "",
        category: "",
        subject: "",
        matchedCount: 0,
        participants: [],
        debateId: null,
        openviduToken: null,
      };
      this.isMatching = false;
      this.isMatched = false;
    },
    toggleMatchingExpanded() {
      this.isExpanded = !this.isExpanded;
    },
  },
});
