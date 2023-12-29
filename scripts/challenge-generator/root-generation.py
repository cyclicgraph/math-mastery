import random
import sympy as sp
import sys

def generate_roots(lower_bound, upper_bound, rating, degree):
    # print("generating " + str(degree) + " degree roots from " + str(lower_bound) + " to " + str(upper_bound))
    if degree < 2:
        print('wrong root degree')
        return
    if lower_bound < 1 or (upper_bound <=  lower_bound):
        print('wrong bounds')
        return

    questions = []
    for i in range(lower_bound + 1, upper_bound):
        human_question = "What's the " + ordinal(degree) + " root of " + str(i * i) + "?"
        latex_equation = "\\sqrt[" + str(degree) + "]{" + str(i * i) + "}="
        question_dict = {
            "human_question": human_question,
            "latex_equation": latex_equation,
            "initial_rating": rating,
            "answer": i
        }

        questions.append(question_dict)
    return questions

def ordinal(num):
    SUFFIXES = {1: 'st', 2: 'nd', 3: 'rd'}
    if 10 <= num % 100 <= 20:
        suffix = 'th'
    else:
        suffix = SUFFIXES.get(num % 10, 'th')
    return str(num) + suffix

print(generate_roots(lower_bound=int(sys.argv[1]), upper_bound=int(sys.argv[2]), rating=int(sys.argv[3]), degree=int(sys.argv[4])))
