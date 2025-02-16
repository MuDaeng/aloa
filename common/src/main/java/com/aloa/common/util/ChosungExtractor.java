package com.aloa.common.util;

import java.util.Optional;

public class ChosungExtractor {
    static String [] INITIAL_STRING = {
            "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ",
            "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ",
            "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ",
            "ㅋ", "ㅌ", "ㅍ", "ㅎ" };


    public static String extractChosung(String stringContent) {
        StringBuilder chosung = new StringBuilder();
        Optional<String> stringContentOp = Optional.ofNullable(stringContent);
        if(stringContentOp.isPresent()) {//전달 문자열 존재
            for(int i = 0; i < stringContentOp.get().length(); i++) {
                String stringLocationInfo = String.valueOf(stringContentOp.get().charAt(i)); //현재 위치의 한글자 추출
                if(stringLocationInfo.matches(".*[가-힣]+.*")) {// 한글일 경우
                    if(stringLocationInfo.charAt(0) >= 0xAC00) // 음절 시작코드(10진값, 문자) : 0xAC00 (44032 ,'가' )
                    {
                        int unicode = stringLocationInfo.charAt(0) - 0xAC00;
                        int index = ((unicode - (unicode % 28))/28)/21;
                        chosung.append(INITIAL_STRING[index]);
                    }

                } else {//한글이 아닐경우
                    chosung.append(stringLocationInfo);//변환없이 그대로 저장
                }
            }
        }

        return chosung.toString();
    }
}
