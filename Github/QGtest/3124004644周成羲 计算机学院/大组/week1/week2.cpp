#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <ctype.h>

#define MAX_SIZE 100

// 定义栈结构
typedef struct {
    char data[MAX_SIZE];
    int top;
} Stack;

// 初始化栈
void initStack(Stack* s) {
    s->top = -1;
}

// 判断栈是否为空
int isEmpty(Stack* s) {
    return s->top == -1;
}

// 判断栈是否已满
int isFull(Stack* s) {
    return s->top == MAX_SIZE - 1;
}

// 入栈
void push(Stack* s, char c) {
    if (!isFull(s)) {
        s->data[++(s->top)] = c;
    }
}

// 出栈
char pop(Stack* s) {
    if (!isEmpty(s)) {
        return s->data[(s->top)--];
    }
    return '\0';
}

// 获取栈顶元素
char peek(Stack* s) {
    if (!isEmpty(s)) {
        return s->data[s->top];
    }
    return '\0';
}

// 判断是否为运算符
int isOperator(char c) {
    return c == '+' || c == '-' || c == '*' || c == '/' || c == '^';
}

// 获取运算符优先级
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

// 中缀表达式转后缀表达式
void infixToPostfix(char* infix, char* postfix) {
    Stack s;
    initStack(&s);
    int i = 0, j = 0;
    char c;

    while ((c = infix[i]) != '\0') {
        // 如果是数字或字母，直接输出
        if (isalnum(c)) {
            postfix[j++] = c;
        }
        // 如果是左括号，压入栈
        else if (c == '(') {
            push(&s, c);
        }
        // 如果是右括号，弹出栈中的运算符直到遇到左括号
        else if (c == ')') {
            while (!isEmpty(&s) && peek(&s) != '(') {
                postfix[j++] = pop(&s);
            }
            if (!isEmpty(&s) && peek(&s) == '(') {
                pop(&s); // 弹出左括号
            }
        }
        // 如果是运算符
        else if (isOperator(c)) {
            while (!isEmpty(&s) && peek(&s) != '(' && 
                   getPriority(peek(&s)) >= getPriority(c)) {
                postfix[j++] = pop(&s);
            }
            push(&s, c);
        }
        i++;
    }

    // 处理栈中剩余的运算符
    while (!isEmpty(&s)) {
        postfix[j++] = pop(&s);
    }
    postfix[j] = '\0';
}

int main() {
    char infix[MAX_SIZE];
    char postfix[MAX_SIZE];

    printf("请输入中缀表达式: ");
    scanf("%s", infix);

    infixToPostfix(infix, postfix);
    printf("后缀表达式: %s\n", postfix);

    return 0;
}
