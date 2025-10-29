Get-ChildItem -Path "src\com\ferrocarriles\gestor\*.java" | ForEach-Object {
    $content = [System.IO.File]::ReadAllText($_.FullName, [System.Text.Encoding]::UTF8)
    
    # Reemplazar caracteres corruptos comunes
    $content = $content `
        -replace 'Ã','ñ' `
        -replace 'Ã','í' `
        -replace 'Ã','ó' `
        -replace 'Ã','é' `
        -replace 'Ãº','ú' `
        -replace 'Ã¡','á' `
        -replace 'Ã','Í' `
        -replace 'Ã"','Ó' `
        -replace 'Ã','É' `
        -replace 'Ãš','Ú' `
        -replace 'Ã'','N' `
        -replace 'Ã¡','Á' `
        -replace 'ðŸ[\x80-\xBF][\x80-\xBF]','[emoji]' `
        -replace 'âž','[+]' `
        -replace 'âœ','[OK]' `
        -replace 'âŒ','[X]' `
        -replace 'âœï','[edit]' `
        -replace 'ðŸ'ï','[delete]' `
        -replace 'ðŸ"','[search]' `
        -replace 'ðŸ"','[list]' `
        -replace 'ðŸ'','[save]' `
        -replace 'ðŸ"','[load]' `
        -replace 'ðŸ"Š','[stats]' `
        -replace 'ðŸ','[clean]' `
        -replace 'âš ï','[warning]' `
        -replace 'ðŸ[\x80-\xBF]{1,3}','[emoji]'
    
    # Guardar sin BOM
    $utf8NoBom = New-Object System.Text.UTF8Encoding $false
    [System.IO.File]::WriteAllText($_.FullName, $content, $utf8NoBom)
    
    Write-Host "Limpiado: $($_.Name)"
    
}