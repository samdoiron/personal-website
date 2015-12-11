#!/usr/bin/env ruby

COMMANDS = {
  'run' => -> { run_server() },
  'dev-bootstrap' => -> { bootstrap() },
  'repl' => -> { repl() },
  'deploy' => -> { deploy() }
}

def lein(*args)
  system("lein #{args.join(' ')}")
end

def repl
  lein("repl")
end

def run_server
  puts "Starting server"
  puts "---------------"
  lein("run")
end

def deploy
  puts "Syncing..."
  `rsync --progress --exclude '.git' -r /Users/samdoiron/Code/site personal:~/site`
  puts "Done."
end

def bootstrap
  puts "WARNING: This is currently not idempotent (can only be run once)"
  print "you're sure you've never run this before? (y/n) "
  response = STDIN.gets().chomp
  until ['y', 'n'].include?(response)
    puts "eh?"
    response = STDIN.gets().chomp
  end

  if response == 'n'
    puts "Good thing I checked!"
    exit(0)
  end

  puts "Installing autoenv"
  `brew install autoenv`
  `echo 'source /usr/local/opt/autoenv/activate.sh' >> ~/.bashrc`

  puts "Installing leiningen"
  `mkdir -p ~/bin`
  `curl https://raw.githubusercontent.com/technomancy/leiningen/stable/bin/lein > ~/bin/lein`
  `chmod u+x ~/bin/lein`
  `echo 'export PATH="$PATH:~/bin"' >> ~/.bashrc`
end

if ARGV.size.zero? || !COMMANDS.include?(ARGV[0])
  puts "Usage: #{__FILE__} <command>"
  puts "Where command:"
  COMMANDS.keys.each do |com_name|
    puts "\t#{com_name}"
  end
else
  COMMANDS[ARGV[0]].()
end
