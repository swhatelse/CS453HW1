#exec("javac ../src/Parse.java")

expressions = []
expressions[0] ='1'
expressions[1] ='1+'
expressions[2] = '1+2'
expressions[3] = '1+2+3'
expressions[4] = '+1'
expressions[5] = '+'
expressions[6] = '++'
expressions[7] = '++1'
expressions[8] = '+++1'
expressions[9] = '1++'
expressions[10] = '1+++'
expressions[11] = '++1++'
expressions[12] = '$'
expressions[13] = '$1'
expressions[14] = '$++1'
expressions[15] = '$++1++'
expressions[16] = '$$$++1++'
expressions[17] = '(1+2)'
expressions[18] = '(1+2) + (3+4)'
expressions[19] = '$(1+2)'
expressions[20] = ')'
expressions[21] = '('
expressions[22] = '(1'
expressions[23] = ')1'
expressions[24] = '1+2)'
expressions[25] = '$(1+2)+3'
expressions[26] = '$1$2'

answers = []
answers[0] ='1 Expression parsed successfully'
answers[1] ='Parse error in line <1>'
answers[2] = '1 2 + Expression parsed successfully'
answers[3] = '1 2 + 3 + Expression parsed successfully'
answers[4] = 'Parse error in line <1>'
answers[5] = 'Parse error in line <1>'
answers[6] = 'Parse error in line <1>'
answers[7] = '1 ++_ Expression parsed successfully'
answers[8] = 'Parse error in line <1>'
answers[9] = '1 _++ Expression parsed successfully'
answers[10] = 'Parse error in line <1>'
answers[11] = '1 _++ ++_ Expression parsed successfully'
answers[12] = 'Parse error in line <1>'
answers[13] = '1 $ Expression parsed successfully'
answers[14] = '1 ++_ $ Expression parsed successfully'
answers[15] = '1 _++ ++_ $ Expression parsed successfully'
answers[16] = '1 _++ ++_ $ $ $ Expression parsed successfully'
answers[17] = '1 2 + Expression parsed successfully'
answers[18] = '1 2 + 3 4 + + Expression parsed successfully'
answers[19] = '1 2 + $ Expression parsed successfully'
answers[20] = 'Parse error in line <1>'
answers[21] = 'Parse error in line <1>'
answers[22] = 'Parse error in line <1>'
answers[23] = 'Parse error in line <1>'
answers[24] = 'Parse error in line <1>'
answers[25] = '1 2 + $ 3 + Expression parsed successfully'
answers[26] = '1 $ 2 $ _ Expression parsed successfully'

passed = 0

(0...expressions.length).each{|i|
  res = ''
  res = `echo '#{expressions[i]}' | java Parse`
  print res
  if res.rstrip == answers[i] then
    passed += 1
  end
}

print "Successfully passed #{passed} on #{expressions.length}\n"
