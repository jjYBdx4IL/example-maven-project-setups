// test functions for native access via JNA

int sum (int *arr, int arrLength)
{
    int sum = 0;
    int i;

    for (i=0; i < arrLength; i++)
        sum += arr[i];

    return sum;
}

int inc(int arg)
{
    return arg + 1;
}