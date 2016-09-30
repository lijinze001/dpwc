$(function () {
    var typeChangeColor = function ($type) {
        switch ($type.val()) {
            case '-1':
                $type.css('color', 'red');
                break;
            case '0':
                $type.css('color', 'orange');
                break;
            case '1':
                $type.css('color', 'orchid');
                break;
            case '2':
                $type.css('color', 'black');
                break;
            default:
                $type.css('color', 'blue');
                break;
        }
    }
    $('select[name=type]').each(function () {
        var $type = $(this);
        typeChangeColor($type);
        $type.change(function () {
            typeChangeColor($type);
        });
    });
    $('input[type=submit]').each(function () {
        var $submit = $(this);
        $submit.click(function () {
            $submit.parent().prev().find('form[role=form]').submit();
        });
    });
});