package com.ghw.maplojbackendjudgeservice.Judge;

import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.CodesandboxFactory;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.codesandbox;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.impl.Examplecodesandboximpl;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.impl.Remotecodesandboximpl;
import com.ghw.maplojbackendjudgeservice.Judge.codesandbox.impl.thirdpartcodesandboximpl;

/**
 * 用户角色枚举
 *
 */
public enum CodesandboxFactoryEnum implements CodesandboxFactory {

    EXAMPLE {
        @Override
        public codesandbox getCodesandbox() {
            return new Examplecodesandboximpl();
        }
    },
    REMOTE {
        @Override
        public codesandbox getCodesandbox() {
            return new Remotecodesandboximpl();
        }
    },
    THIRD_PART {
        @Override
        public codesandbox getCodesandbox() {
            return new thirdpartcodesandboximpl();
        }
    };

    CodesandboxFactoryEnum() {}
}
