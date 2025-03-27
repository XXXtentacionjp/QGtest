#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_SIZE 100

// ������ֵջ�ṹ
typedef struct {
    double data[MAX_SIZE];
    int top;
} NumStack;

// ���������ջ�ṹ
typedef struct {
    char data[MAX_SIZE];
    int top;
} Stack;

// ��ʼ����ֵջ
void initNumStack(NumStack* s) {
    s->top = -1;
}

// ��ʼ�������ջ
void initStack(Stack* s) {
    s->top = -1;
}

// �ж���ֵջ�Ƿ�Ϊ��
int isNumStackEmpty(NumStack* s) {
    return s->top == -1;
}

// �ж������ջ�Ƿ�Ϊ��
int isEmpty(Stack* s) {
    return s->top == -1;
}

// �ж������ջ�Ƿ�����
int isFull(Stack* s) {
    return s->top == MAX_SIZE - 1;
}

// ��ֵջ��ջ
void pushNum(NumStack* s, double num) {
    if (s->top < MAX_SIZE - 1) {
        s->data[++(s->top)] = num;
    }
}

// �����ջ��ջ
void push(Stack* s, char c) {
    if (!isFull(s)) {
        s->data[++(s->top)] = c;
    }
}

// ��ֵջ��ջ
double popNum(NumStack* s) {
    if (!isNumStackEmpty(s)) {
        return s->data[(s->top)--];
    }
    return 0;
}

// �����ջ��ջ
char pop(Stack* s) {
    if (!isEmpty(s)) {
        return s->data[(s->top)--];
    }
    return '\0';
}

// ��ȡ�����ջ��Ԫ��
char peek(Stack* s) {
    if (!isEmpty(s)) {
        return s->data[s->top];
    }
    return '\0';
}

// �ж��Ƿ�Ϊ�����
int isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
}

// ��ȡ��������ȼ�
int getPriority(char c) {
    switch (c) {
        case '+':
        case '-':
            return 1;
        case '*':
        case '/':
            return 2;
        case '^':
            return 3;
        default:
            return 0;
    }
}

// ִ������
double calculate(double a, double b, char op) {
    switch (op) {
        case '+': return a + b;
        case '-': return a - b;
        case '*': return a * b;
        case '/': return b != 0 ? a / b : 0;
        case '^': {
            double result = 1;
            for (int i = 0; i < b; i++) {
                result *= a;
            }
            return result;
        }
        default: return 0;
    }
}

// �����׺���ʽ��ֵ
double evaluatePostfix(char* postfix) {
    NumStack s;
    initNumStack(&s);
    int i = 0;
    char c;

    while ((c = postfix[i]) != '\0') {
        if (isdigit(c)) {
            // ��������֣�ֱ����ջ
            pushNum(&s, c - '0');
        } else if (isOperator(c)) {
            // �����������������������������м���
            double b = popNum(&s);
            double a = popNum(&s);
            double result = calculate(a, b, c);
            pushNum(&s, result);
        }
        i++;
    }

    return popNum(&s);
}

// ��׺���ʽת��׺���ʽ
void infixToPostfix(char* infix, char* postfix) {
    Stack s;
    initStack(&s);
    int i = 0, j = 0;
    char c;

    while ((c = infix[i]) != '\0') {
        // ��������ֻ���ĸ��ֱ�����
        if (isalnum(c)) {
            postfix[j++] = c;
        }
        // ����������ţ�ѹ��ջ
        else if (c == '(') {
            push(&s, c);
        }
        // ����������ţ�����ջ�е������ֱ������������
        else if (c == ')') {
            while (!isEmpty(&s) && peek(&s) != '(') {
                postfix[j++] = pop(&s);
            }
            if (!isEmpty(&s) && peek(&s) == '(') {
                pop(&s); // ����������
            }
        }
        // ����������
        else if (isOperator(c)) {
            while (!isEmpty(&s) && peek(&s) != '(' && 
                   getPriority(peek(&s)) >= getPriority(c)) {
                postfix[j++] = pop(&s);
            }
            push(&s, c);
        }
        i++;
    }

    // ����ջ��ʣ��������
    while (!isEmpty(&s)) {
        postfix[j++] = pop(&s);
    }
    postfix[j] = '\0';
}

int main() {
    char infix[MAX_SIZE];
    char postfix[MAX_SIZE];

    printf("��������׺���ʽ����֧�ֵ������ֺ��������㣩: ");
    scanf("%s", infix);

    infixToPostfix(infix, postfix);
    printf("��׺���ʽ: %s\n", postfix);
    
    double result = evaluatePostfix(postfix);
    printf("������: %.2f\n", result);

    return 0;
}
