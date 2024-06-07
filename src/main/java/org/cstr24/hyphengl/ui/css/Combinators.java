package org.cstr24.hyphengl.ui.css;

import com.helger.commons.collection.impl.ICommonsList;
import com.helger.css.decl.CSSSelector;
import com.helger.css.decl.CSSSelectorAttribute;
import com.helger.css.decl.ECSSExpressionOperator;
import com.helger.css.decl.ICSSSelectorMember;
import org.cstr24.hyphengl.ui.windowing.BaseControl;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public final class Combinators {
    public static final Combinator NEXT_SIBLING = new Combinator("+", "Next Sibling",
        (list) -> list.stream().map(BaseControl::getNextSibling).toList()
    );

    public static final Combinator CHILD = new Combinator(">", "Child",
        (list) -> list.stream().flatMap(control -> control.getChildren().stream()).collect(Collectors.toList())
    );

    public static final Combinator SUBSEQUENT_SIBLING = new Combinator("~", "Subsequent Sibling", (list) -> {
        return null;
    });

    public static final Combinator DESCENDANT = new Combinator(" ", "Descendant",
        (list) -> {
            List<BaseControl> results = new ArrayList<>();
            for (BaseControl control : list){
                results.addAll(control.getAllDescendents());
            }
            return results;
        }
    );

    public static final HashMap<String, Combinator> combinators = new HashMap<>();
    public static void registerCombinator(Combinator comb){
        combinators.put(comb.operator, comb);
    }
    public static boolean isKnownCombinator(String test){
        return combinators.containsKey(test);
    }
    public static Combinator get(String comb){
        return combinators.get(comb);
    }

    static {
        registerCombinator(NEXT_SIBLING);
        registerCombinator(CHILD);
        registerCombinator(SUBSEQUENT_SIBLING);
        registerCombinator(DESCENDANT);
    }

    //combinator scope - sibling (immediately next in queue), subsequents (any other than the first), child (DIRECT descendant), descendant (anywhere in the pile)

    public static class Combinator{
        private String operator = "";
        private String name = "";
        private Function<List<BaseControl>, List<BaseControl>> combinatorFunction;

        public Combinator(String operatorString, String nameString, Function<List<BaseControl>, List<BaseControl>> function){
            this.operator = operatorString;
            this.name = nameString;

            combinatorFunction = function;
        }

        public List<BaseControl> applyCombinator(List<BaseControl> controlList){
            //if given a control list - this would have to be a list that's been filtered thus far.
            //return a list that then matches the rules within selector from start - end

            //combinator to decide which controls out of 'controlList' to then go and test
            List<BaseControl> combinatorResultList = combinatorFunction.apply(controlList);
            return combinatorResultList;
        }

        public String getName() {
            return name;
        }

        public String getOperator() {
            return operator;
        }
    }

    //new approach - run all rules on the root container and see what applies, that way i don't have to worry about working my way back up a hierarchy at the per-control level
}
