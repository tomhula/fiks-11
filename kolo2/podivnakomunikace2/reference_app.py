str1 = 'fit\0'
str2 = 'fiks\0'

differences = 0
str1_terminated = 0
str2_terminated = 0
ch1_pointer = 0
ch2_pointer = 0

def increment_pointers():
    global ch1_pointer, ch2_pointer
    ch1_pointer += 1
    ch2_pointer += 1

def increment_pointers_and_differences():
    increment_pointers()
    global differences
    differences += 1

while True:
    char1 = str1[ch1_pointer] if ch1_pointer < len(str1) else 'UNDEFINED'
    char2 = str2[ch2_pointer] if ch2_pointer < len(str2) else 'UNDEFINED'

    if char1 == '\0':
        str1_terminated = 1
    if char2 == '\0':
        str2_terminated = 1

    if str1_terminated == 1:
        if str2_terminated == 1:
            break

    if char1 != char2:
        increment_pointers_and_differences()
        continue

    if str1_terminated == 1:
        increment_pointers_and_differences()
        continue

    if str2_terminated == 1:
        increment_pointers_and_differences()
        continue

    increment_pointers()

print(differences)