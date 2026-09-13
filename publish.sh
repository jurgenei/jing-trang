VERSION="v$(cat src/main/resources/version.xml | sed -E 's#<version>([^<]+)</version>#\1#')"
echo publishing $VERSION
git tag -d $VERSION || true
git push origin :refs/tags/$VERSION || true
git tag $VERSION
git push origin $VERSION
