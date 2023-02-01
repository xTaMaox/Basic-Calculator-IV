class Solution {
    
    Map<String, Integer> vars = new HashMap<>();
    char[] chars;
    int i = 0;
    
    public List<String> basicCalculatorIV(String expression, String[] evalvars, int[] evalints) {
        this.chars = expression.toCharArray();
        
        for (int k = 0; k < evalvars.length; k++) {
            vars.put(evalvars[k], evalints[k]);
        }
        
		//Consider order of term first, then lexicographic comparison
        Map<String, Integer> resultMap = expression();
        PriorityQueue<String> queue = 
            new PriorityQueue<>((a,b) -> {
                    int orderDiff = b.split("\\*").length - a.split("\\*").length;
                    if (orderDiff != 0) {
                        return orderDiff;
                    }
                    else {
                        return a.compareTo(b);
                    }
                });
        
        for (String s: resultMap.keySet()) {
            queue.offer(s);
        }
        
        List<String> result = new ArrayList<>();
        while (!queue.isEmpty()) {
            String var = queue.poll();
            if (var.equals("")) {
                continue;
            }
            
            StringBuilder sb = new StringBuilder();
            int value = resultMap.get(var);
            if (value != 0) {
                sb.append(value).append("*").append(var);
                result.add(sb.toString());
            }
        }
        
       if (resultMap.containsKey("") && resultMap.get("") != 0) {
            result.add(resultMap.get("").toString());
        }
        return result;
    }
    
   public Map<String, Integer> add(Map<String, Integer> m, Map<String, Integer> p, boolean subtract) {
        for (Map.Entry<String, Integer> entry: p.entrySet()) {
            String key = entry.getKey();
            int val = entry.getValue();
            if (subtract) {
                val *= -1;
            }
            m.put(key, m.getOrDefault(key, 0) + val);
        }
        return m;
    }
    
    public Map<String, Integer> multiply(Map<String, Integer> m, Map<String, Integer> p) {
        Map<String, Integer> result = new HashMap<>();
        for (Map.Entry<String, Integer> outer: m.entrySet()) {
            String outerVar = outer.getKey();
            Integer outerVal = outer.getValue();
            for(Map.Entry<String, Integer> inner: p.entrySet()) {
                String innerVar = inner.getKey();
                Integer innerVal = inner.getValue();
                
                String var = getVarName(outerVar, innerVar);
                result.put(var, result.getOrDefault(var, 0) + innerVal * outerVal);
            }
        }
        return result;
    }
    
	//Sort variables of multiplication term lexicographically
    public String getVarName(String a, String b) {
        String[] first = a.split("\\*");
        String[] second = b.split("\\*");
        String[] combine = new String[first.length + second.length];
        int k = 0;
        for (String s: first) {
            combine[k] = s;
            k++;
        }
        for (String s: second) {
            combine[k] = s;
            k++;
        }
        
        Arrays.sort(combine);
        StringBuilder sb = new StringBuilder();
        for (String s: combine) {
            if (s.equals("")) {
                continue;
            }
            sb.append(s).append("*");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }
    
	//Look at current char without advancing index
    public char peek() {
        while(i < chars.length && chars[i] == ' ') {
            i++;
        }
        if (i >= chars.length) {
            return 0;
        }
        return chars[i];
    }
	
    //Get current char and advance index
    public char getNext() {
        if (i >= chars.length) {
            return 0;
        }
        return chars[i++];
    }
    
    public int number() {
        int result = 0;
        while (peek() >= '0' && peek() <= '9') {
            result = result * 10 + (getNext() - '0');
        }
        return result;
    }
    
    public String var() {
        String result = "";
        StringBuilder sb = new StringBuilder();
        while (peek() >= 'a' && peek() <= 'z') {
            sb.append(getNext());
        }
        return sb.toString();
    }
    
    public Map<String, Integer> factor() {
        Map<String, Integer> result = new HashMap<>();
        result.put("", 1);
        if (peek() == '-'){
            result.put("", -1);
            getNext();
        }
        if (peek() == '('){
            getNext();
            result = multiply(result, expression());
            getNext();
        }
        else if (peek() >= '0' && peek() <= '9') {
            Map<String, Integer> num = new HashMap<>();
            num.put("", number());
            result = multiply(result, num);
        }
        else if (peek() >= 'a' && peek() <= 'z') {
            String var = var();
            if (vars.containsKey(var)) {
                Map<String, Integer> num = new HashMap<>();
                num.put("", vars.get(var));
                result = multiply(result, num);
            }
            else {
                Map<String, Integer> num = new HashMap<>();
                num.put(var, 1);
                result = multiply(result, num);
            }
        }
        return result;
    }
    
    public Map<String, Integer> term() {
        Map<String, Integer> result = factor();
        while (peek() == '*'){
            if (getNext() == '*') {
                result = multiply(result, factor());
            }
        }
        return result;
    }
    
    public Map<String, Integer> expression() {
        Map<String, Integer> result = new HashMap<>();
        result.put("", 0);
        if (peek() != '+' || peek() != '-') {
            result = add(result, term(), false);
        }
        while (peek() == '+' || peek() == '-') {
            if (getNext() == '+') {
                result = add(result, term(), false);
            }
            else {
                result = add(result, term(), true);
            }
        }
        return result;
    }
}