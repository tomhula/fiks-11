str1 = 'abc\0'
str2 = 'aaaaaa\0'

differences = 0
str1_terminated = 0
str2_terminated = 0
i = 0

while True:
    char1 = str1[i] if i < len(str1) else 'UNDEFINED'
    char2 = str2[i] if i < len(str2) else 'UNDEFINED'

    if char1 == '\0':
        str1_terminated = 1
    if char2 == '\0':
        str2_terminated = 1

    if str1_terminated:
        if str2_terminated:
            break

    i += 1

    if char1 != char2:
        differences += 1
        continue

    if str1_terminated == 1:
        differences += 1
        continue

    if str2_terminated == 1:
        differences += 1
        continue


print(differences)