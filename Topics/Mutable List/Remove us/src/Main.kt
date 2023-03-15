fun solution(elements: MutableList<String>, index: Int): MutableList<String> {
    elements.removeAt(index)
    return elements
}

val mutListMD = mutableListOf(
    mutableListOf(mutableListOf<Boolean>(true, true), mutableListOf<Int>(1, 1)),
    mutableListOf(mutableListOf<Int>(1, 1), mutableListOf<Boolean>(true, true))
)