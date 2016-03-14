var gulp = require('gulp'),
  livereload = require('gulp-livereload'),
  sass = require('gulp-sass'),
  autoprefixer = require('gulp-autoprefixer'),
  cleanCss = require('gulp-clean-css');

var paths = {
  styles: ['static/styles/**/*.scss']
}

gulp.task('styles', function () {
  return gulp.src(paths.styles)
    .pipe(sass().on('error', sass.logError))
    .pipe(cleanCss())
    .pipe(autoprefixer({
      browsers: ['last 2 versions']
    }))
    .pipe(gulp.dest('static/'))
    .pipe(livereload());
});

gulp.task('watch', function () {
  livereload.listen();
  gulp.watch(paths.styles, ['styles']);
});
