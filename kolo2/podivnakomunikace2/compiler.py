import os
import re
import sys
from typing import Self, Protocol

from setuptools.namespaces import flatten

class Macro(Protocol):
    def get_name(self) -> str:
        ...
    def get_syntax(self) -> str:
        ...
    def get_description(self) -> str:
        ...
    def expand(self, *args: str) -> list[str]:
        ...

class AddToMacro(Macro):
    def get_name(self) -> str:
        return "ADD_TO"

    def get_syntax(self) -> str:
        return "ADD_TO <stack_size> <target_index (from bottom)> <amount>"

    def get_description(self) -> str:
        return "Adds <amount> to the element at <target_index> from the bottom of the stack of size <stack_size>"

    def expand(self, *args) -> list[str]:
        stack_size = int(args[0])
        target_index = int(args[1])
        amount = int(args[2])
        builder = FslBuilder()
        # Get target on top
        target_to_top_rotate_num = stack_size - target_index
        builder.rotate(target_to_top_rotate_num)

        for i in range(stack_size):
            if i == stack_size - 1:
                builder.push(-amount)
                break
            else:
                builder.push(0)
            n = stack_size - i
            builder.rotate(n)
            builder.rotate(n - 1)

        builder.vecsub()
        # Get target back to its original position
        builder.rotate(target_to_top_rotate_num)
        return builder.build()

class IncludeMacro(Macro):
    def get_name(self) -> str:
        return "INCLUDE"

    def get_syntax(self) -> str:
        return "INCLUDE <file_path>"

    def get_description(self) -> str:
        return "Includes the content of the file at <file_path>"

    def expand(self, *args) -> list[str]:
        file_path = args[0]
        with open(file_path) as file:
            return file.read().split(os.linesep)

class PopMacro(Macro):
    def get_name(self) -> str:
        return "POP"

    def get_syntax(self) -> str:
        return "POP"

    def get_description(self) -> str:
        return "Pops the top element from the stack"

    def expand(self, *args) -> list[str]:
        builder = FslBuilder()
        builder.push(1)
        builder.delete()
        return builder.build()

class FslBuilder:
    def __init__(self):
        self.lines = []

    def build(self) -> list[str]:
        return self.lines

    def append_instruction(self, instruction, *args) -> Self:
        self.lines.append(instruction + " " + " ".join(map(str, args)))
        return self

    def append(self, anything: any) -> Self:
        self.lines.append(str(anything).split(os.linesep))
        return self

    def append_with_nl(self, anything: any) -> Self:
        return self.append(anything).append(os.linesep)

    def comment(self, *content, prepend_space=True) -> Self:
        content = list(flatten(map(lambda c: c.split(os.linesep), content)))
        for comment in content:
            if prepend_space:
                self.append_with_nl("# " + comment)
            else:
                self.append_with_nl("#" + comment)
        return self

    def push(self, arg: int) -> Self:
        return self.append_instruction("PUSH", arg)

    def rotate(self, arg: int, force: bool = False) -> Self:
        if force or arg > 1:
            self.append_instruction("ROTATE", arg)
        return self

    def vecsub(self) -> Self:
        return self.append_instruction("VECSUB")

    def delete(self) -> Self:
        return self.append_instruction("DELETE")


def expand_macros(source_text: str, macros: list[Macro]) -> str:
    lines = source_text.split(os.linesep)
    result_lines = []
    for line in lines:
        for macro in macros:
            match = re.match(r"(\s*)(" + macro.get_name() + r"(?:\s+(.*))?)", line)
            if match:
                indent = match.group(1)
                args_str = match.group(3)
                if args_str:
                    args = args_str.split()
                else:
                    args = []
                macro_lines = macro.expand(*args)
                result_lines.append(indent + "#@ " + match.group(2))
                result_lines += map(lambda l: indent + l, macro_lines)
                result_lines.append(indent + "#@@")
                break
        else:
            result_lines.append(line)
    return os.linesep.join(result_lines)

if __name__ == '__main__':
    source_path = sys.argv[1]

    with open(source_path) as file:
        source_text = file.read()

    macros = [AddToMacro(), IncludeMacro(), PopMacro()]
    expanded_text = expand_macros(source_text, macros)
    print(expanded_text, end="")


