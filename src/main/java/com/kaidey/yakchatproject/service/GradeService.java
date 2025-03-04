package com.kaidey.yakchatproject.service;

import com.kaidey.yakchatproject.entity.User;
import com.kaidey.yakchatproject.entity.enums.GradeType;
import org.springframework.stereotype.Service;

@Service
public class GradeService {

    public GradeType calculateGrade(User user) {
        int questions = user.getQuestionCount();
        int accepted = user.getAcceptedCount();
        int likes = user.getLikeCount();
        int purchases = user.getPurchasedMaterialCount();
        int sales = user.getSoldMaterialCount();

        if (questions >= 1000 || accepted >= 500 || likes >= 5000 || sales >= 5000 || purchases >= 200) {
            return GradeType.GOLD;
        } else if (questions >= 500 || accepted >= 300 || likes >= 3000 || sales >= 1000 || purchases >= 100) {
            return GradeType.BLACK;
        } else if (questions >= 150 || accepted >= 100 || likes >= 1000 || sales >= 500 || purchases >= 30) {
            return GradeType.RED;
        } else if (questions >= 75 || accepted >= 50 || likes >= 500 || sales >= 200 && purchases >= 15) {
            return GradeType.ORANGE;
        } else if (questions >= 30 || accepted >= 20 || likes >= 100 || sales >= 50 || purchases >= 6) {
            return GradeType.YELLOW;
        } else if (questions >= 10 || accepted >= 5 || sales >= 10 || purchases >= 2) {
            return GradeType.GREEN;
        } else if (questions >= 5 || purchases >= 1) {
            return GradeType.BLUE;
        }
        return GradeType.GRAY;
    }

    public void updateUserGrade(User user) {
        user.setUserGrade(calculateGrade(user));
    }
}
