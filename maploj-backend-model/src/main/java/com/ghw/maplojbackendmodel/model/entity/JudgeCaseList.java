package com.ghw.maplojbackendmodel.model.entity;

import com.ghw.maplojbackendmodel.model.dto.question.JudgeCase;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JudgeCaseList {
    private List<JudgeCase> judgeCaseList;
}
