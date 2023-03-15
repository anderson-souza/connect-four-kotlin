// write your code here
val numbers = mutableListOf<Int>(1,2,3)
fun main() {

    val str1 = "aaabbcccdaa"
    var str2 = " "

    for (ch in str1) {
        if (ch != str2.last()) {
            str2 += ch
        }
    }
}
