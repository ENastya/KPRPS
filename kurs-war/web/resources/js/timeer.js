function startTimer(id) {
    var my_timer = document.getElementById("j_idt13:my_timer");
    var time = my_timer.innerHTML;
    var arr = time.split(":");
    var h = arr[0];
    var m = arr[1];
    var s = arr[2];
    if (s == 59) {
        if (m == 59) {
            if (h == 23) {
                alert("Кажется ты слишком долго работаешь");
                window.location.reload();
                return;
            } else
                h++;
            m = 0;
            if (h < 10)
                h = "0" + h;

        } else
            m++;
        if (m < 10)
            m = "0" + m;
        s = 0;

    } else
        s++;
    if (s < 10)
        s = "0" + s;
    document.getElementById("j_idt13:my_timer").innerHTML = h + ":" + m + ":" + s;
    setTimeout(startTimer, 1000);

}
