The described procedures are the final ones, leaving all the failed attempts.

# .hashes.txt

I wanted to find out what are the hashes from and if they match. I first thought they might be hashes of the provided files, however that did not match.
Then I tried hashing flags I have already found and the hashes did in fact match.
Revealing the hashes are hashes of the flags.

# image

After using the linux `strings` utility on the image, I could find the following in the output:
```
---BE
GIN F
IKS--
-0111
01000
... many more bits ...
00110
00000
10001
0----
END F
IKS--
```
I put the binary into a binary to text converter and got the following message:
```
that was easy huh: "1001100110010110100101001000110010000100100101101001000110001001110011001000110111001000110011001001101110100000110001111100101111001000110010111100100111001010110010101100100110000010"
```
However, the following binary converted to text did not result in a string.
Then I divided the bits into bytes and noticed that each byte starts with a `1`.
Which is weird, because in ascii string each byte should start with a `0` and if it was UTF-8, there should be at least some bytes with a `0` at the start.
Since in ascii each byte starts with a `0`, and here each byte started with a `1`, I tried inverting the binary.
When inverting the binary and converting it to text, I got the flag.

# wav

# network

I first took a look at the file type, which I was not familiar with.
Found out it is a recording of a network traffic.
I then tried opening it in Wireshark, which did work.
Examining the packets, I found a telnet communication, where the client sent the flag character by character.

# zip