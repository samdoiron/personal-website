(function () {
  var $ = document.querySelectorAll.bind(document),
    $_ = document.querySelector.bind(document);

  Element.prototype.on = Element.prototype.addEventListener;

  document.addEventListener('DOMContentLoaded', function () {
    $_('.oxford-comma').on('mouseenter', function () {
      $_('.comma').classList.add('active');
    });

    $_('.oxford-comma').on('mouseleave', function () {
      $_('.comma').classList.remove('active');
    });
  })
}())
