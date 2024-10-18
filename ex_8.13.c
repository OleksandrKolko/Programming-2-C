#include <stdio.h>
#include <math.h>

int main() {
    int n, m;
    printf("Enter n and m: \n");
    scanf("%d %d", &n, &m);
    int matrix1[n][m], matrix2[m][n], matrix3[n][n];
    for (int i = 0; i < n; i++) {
        printf("Enter matrix1[%d] = \n", i);
        for (int j = 0; j < m; j++) {
            scanf("%d", &matrix1[i][j]);
        }
    }
    for (int i = 0; i < m; i++) {
        printf("Enter matrix2[%d] = \n", i);
        for (int j = 0; j < n; j++) {
            scanf("%d", &matrix2[i][j]);
        }
    }
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            matrix3[i][j] = 0;
            for (int k = 0; k < m; ++k) {
                matrix3[i][j] += matrix1[i][k] * matrix2[k][j];
            }
        }
    }
    for (int i = 0; i < n; ++i) {
        for (int j = 0; j < n; ++j) {
            printf("%d ", matrix3[i][j]);
        }
        printf("\n");
    }

}