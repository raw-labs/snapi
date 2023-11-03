gh api -XPATCH "repos/raw-labs/snapi" -f default_branch="$(git symbolic-ref --short HEAD)" > /dev/null
gh pr comment 254 -b "/publish"

wait 30
gh api -XPATCH "repos/raw-labs/snapi" -f default_branch="main" > /dev/null