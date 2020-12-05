$timesToRun = 100;
$wins = 0;
$loses = 0;
$draws = 0;

for ($i = 1; $i -le $timesToRun; $i++) {
    Start-Process javac -ArgumentList "-cp game_engine.jar Agent.java"
    $seed = Get-Random;
    $consoleOutput = java -jar game_engine.jar 0 game.gmk.GomokuGame $seed 15 15 0.1 2000 game.gmk.players.GreedyPlayer Agent
    $resultLine = $consoleOutput.Split([Environment]::NewLine) | Select -Last 1
    $resultValue = [int]$resultLine.Split(" ")[3];
    Write-Output "Game #$i : $resultValue";
    if($resultValue -gt 0) {
        $wins++;
    }
    elseif($resultValue -eq 0) {
        $draws++;
    }
    elseif($resultValue -lt 0){
        $loses++;;
    }
}

Write-Output "Wins: $wins";
Write-Output "Draws: $draws";
Write-Output "Losses: $loses";